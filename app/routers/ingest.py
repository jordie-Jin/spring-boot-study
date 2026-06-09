import tempfile
from pathlib import Path

from fastapi import APIRouter, Depends, File, HTTPException, UploadFile
from fastapi.concurrency import run_in_threadpool
from langchain_chroma import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

from ..dependencies import RagSettings, get_rag_settings, get_vectorstore
from ..schemas import IngestResponse

router = APIRouter(prefix="/rag", tags=["rag"])


def _ingest_pdf(
    data: bytes,
    filename: str,
    store: Chroma,
    settings: RagSettings,
) -> IngestResponse:
    with tempfile.NamedTemporaryFile(suffix=".pdf", delete=False) as tmp:
        tmp.write(data)
        tmp_path = Path(tmp.name)

    try:
        pages = PyPDFLoader(str(tmp_path)).load()
    finally:
        tmp_path.unlink(missing_ok=True)

    if not pages:
        raise HTTPException(
            status_code=422,
            detail="Could not extract text from the PDF.",
        )

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=settings.chunk_size,
        chunk_overlap=settings.chunk_overlap,
    )
    chunks = splitter.split_documents(pages)
    for chunk in chunks:
        chunk.metadata["source"] = filename

    store.add_documents(chunks)
    return IngestResponse(
        filename=filename,
        pages=len(pages),
        chunks_added=len(chunks),
        total_chunks=store._collection.count(),
    )


@router.post("/ingest", response_model=IngestResponse)
async def ingest(
    file: UploadFile = File(..., description="PDF file to ingest"),
    settings: RagSettings = Depends(get_rag_settings),
) -> IngestResponse:
    filename = file.filename or "uploaded.pdf"
    if not filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=415, detail="Only PDF files are supported.")

    data = await file.read()
    if not data:
        raise HTTPException(status_code=422, detail="The uploaded file is empty.")

    return await run_in_threadpool(
        _ingest_pdf,
        data,
        filename,
        get_vectorstore(),
        settings,
    )
