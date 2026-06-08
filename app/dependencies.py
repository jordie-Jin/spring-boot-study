from functools import lru_cache

from langchain_openai import ChatOpenAI
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    openai_api_key: str = ""
    model_name: str = "gpt-5-nano"
    port: int = 8000
    request_timeout: float = 30.0
    jwt_secret: str = "please-change-this-to-a-32-byte-minimum-secret-key"

@lru_cache
def get_settings() -> Settings:
    return Settings()

@lru_cache
def get_llm() -> ChatOpenAI:
    settings = get_settings()
    return ChatOpenAI (
        model=settings.model_name,
        api_key=settings.openai_api_key,
        timeout=settings.request_timeout,
    )
