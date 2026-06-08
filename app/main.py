from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import logging

from .schemas import ChatRequest, ChatResponse

from .errors import handle_unexpected
from .middleware import add_process_time, require_bearer_token
from .schemas import ChatRequest, ChatResponse

from .routers import chat_langchain, chat_crew

app = FastAPI(title="AI Backend", version="1.0.0")

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s — %(message)s",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["Content-Type", "Authorization"],
)
app.middleware("http")(require_bearer_token)
app.middleware("http")(add_process_time)

app.include_router(chat_langchain.router)
app.include_router(chat_crew.router)

app.add_exception_handler(Exception, handle_unexpected)

@app.get("/health", tags=["meta"])
def health():
    """헬스 체크 — Docker/k8s liveness 용도."""
    return {"status": "ok"}

@app.post("/echo", tags=["meta"])
def echo(req: ChatRequest) -> ChatResponse:
    """Pydantic v2 검증 시연용 echo 엔드포인트."""
    return ChatResponse(answer=req.prompt, model="echo-1")
