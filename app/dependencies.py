from functools import lru_cache
from pathlib import Path

from langchain_chroma import Chroma
from langchain_core.vectorstores import VectorStoreRetriever
from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings
from pydantic_settings import BaseSettings, SettingsConfigDict


ROOT = Path(__file__).resolve().parent.parent


class Settings(BaseSettings):
    openai_api_key: str = ""
    model_name: str = "gpt-5-nano"
    port: int = 8000
    request_timeout: float = 30.0
    jwt_secret: str = "please-change-this-to-a-32-byte-minimum-secret-key"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


class RagSettings(BaseSettings):
    openai_api_key: str = ""
    embedding_model: str = "text-embedding-3-small"
    chroma_dir: str = str(ROOT / "chroma_db")
    rag_top_k: int = 3
    chunk_size: int = 500
    chunk_overlap: int = 50

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


@lru_cache
def get_settings() -> Settings:
    return Settings()


@lru_cache
def get_llm() -> ChatOpenAI:
    settings = get_settings()
    return ChatOpenAI(
        model=settings.model_name,
        api_key=settings.openai_api_key,
        timeout=settings.request_timeout,
    )


@lru_cache
def get_rag_settings() -> RagSettings:
    return RagSettings()


@lru_cache
def get_embeddings() -> OpenAIEmbeddings:
    settings = get_rag_settings()
    return OpenAIEmbeddings(
        model=settings.embedding_model,
        api_key=settings.openai_api_key,
    )


@lru_cache
def get_vectorstore() -> Chroma:
    settings = get_rag_settings()
    return Chroma(
        persist_directory=settings.chroma_dir,
        embedding_function=get_embeddings(),
    )


def get_retriever() -> VectorStoreRetriever:
    settings = get_rag_settings()
    return get_vectorstore().as_retriever(
        search_kwargs={"k": settings.rag_top_k},
    )
