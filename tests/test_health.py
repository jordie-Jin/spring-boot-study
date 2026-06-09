import base64
import hashlib
import hmac
import json

from fastapi.testclient import TestClient

from app.main import app


def _encode(value: dict) -> str:
    data = json.dumps(value, separators=(",", ":")).encode()
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()


def _authorization() -> dict[str, str]:
    header = _encode({"alg": "HS256", "typ": "JWT"})
    payload = _encode({"sub": "test-user"})
    signing_input = f"{header}.{payload}"
    signature = hmac.new(
        b"please-change-this-to-a-32-byte-minimum-secret-key",
        signing_input.encode(),
        hashlib.sha256,
    ).digest()
    encoded_signature = base64.urlsafe_b64encode(signature).rstrip(b"=").decode()
    return {"Authorization": f"Bearer {signing_input}.{encoded_signature}"}


client = TestClient(app)


def test_health_returns_ok():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_echo_validates_prompt_length():
    response = client.post("/echo", json={"prompt": ""}, headers=_authorization())

    assert response.status_code == 422


def test_rag_routes_are_registered():
    paths = client.get("/openapi.json").json()["paths"]

    assert "/rag/ingest" in paths
    assert "/rag/chat" in paths
