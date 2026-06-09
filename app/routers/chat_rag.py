from crewai import Agent, Crew, Process, Task
from crewai import LLM as CrewLLM
from crewai.tools import tool
from fastapi import APIRouter, Depends

from ..dependencies import Settings, get_retriever, get_settings
from ..schemas import ChatResponse, RagRequest

router = APIRouter(prefix="/rag", tags=["rag"])


@tool("company_doc_search")
def company_doc_search(query: str) -> str:
    """Search uploaded company documents for relevant content."""
    docs = get_retriever().invoke(query)
    if not docs:
        return "No relevant document was found. Upload a PDF first."
    return "\n\n".join(
        f"[source: {doc.metadata.get('source', '?')}]\n{doc.page_content}"
        for doc in docs
    )


def _build_crew(question: str, settings: Settings) -> Crew:
    llm = CrewLLM(
        model=f"openai/{settings.model_name}",
        api_key=settings.openai_api_key,
    )
    agent = Agent(
        role="HR assistant",
        goal="Answer employee questions accurately from company documents",
        backstory="You are an HR specialist familiar with company policies.",
        tools=[company_doc_search],
        llm=llm,
        verbose=False,
        allow_delegation=False,
    )
    task = Task(
        description=(
            f"Answer this employee question in Korean: '{question}'. "
            "Search company documents first and only use retrieved evidence. "
            "If the documents do not contain the answer, say that you do not know."
        ),
        expected_output="A Korean answer grounded in company documents",
        agent=agent,
    )
    return Crew(
        agents=[agent],
        tasks=[task],
        process=Process.sequential,
        verbose=False,
    )


@router.post("/chat", response_model=ChatResponse)
async def chat_rag(
    req: RagRequest,
    settings: Settings = Depends(get_settings),
) -> ChatResponse:
    result = await _build_crew(req.question, settings).kickoff_async()
    return ChatResponse(answer=str(result), model=f"rag-{settings.model_name}")
