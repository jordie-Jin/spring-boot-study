import base64
import hashlib
import hmac
import json
import time

from fastapi import Request
from starlette.responses import JSONResponse

from .dependencies import get_settings


PUBLIC_PATHS = {
    "/health",
    "/docs",
    "/redoc",
    "/openapi.json",
}


async def require_bearer_token(request: Request, call_next):
    if request.method == "OPTIONS" or request.url.path in PUBLIC_PATHS:
        return await call_next(request)

    token = _resolve_bearer_token(request.headers.get("Authorization"))
    if token is None:
        return _unauthorized("Missing bearer token")

    try:
        claims = _verify_jwt(token, get_settings().jwt_secret)
    except ValueError as exc:
        return _unauthorized(str(exc))

    request.state.user = claims.get("sub")
    request.state.jwt_claims = claims
    return await call_next(request)

async def add_process_time(request: Request, call_next):
    start = time.perf_counter()
    response = await call_next(request)
    elapsed = time.perf_counter() - start
    response.headers["X-Process-Time"] = f"{elapsed:.4f}"
    return response


def _resolve_bearer_token(header: str | None) -> str | None:
    if header is None:
        return None

    parts = header.strip().split(None, 1)
    if len(parts) != 2 or parts[0].lower() != "bearer":
        return None

    token = parts[1].strip()
    return token or None


def _verify_jwt(token: str, secret: str) -> dict:
    parts = token.split(".")
    if len(parts) != 3:
        raise ValueError("Invalid bearer token")

    signing_input = f"{parts[0]}.{parts[1]}".encode("ascii")
    expected_signature = hmac.new(
        secret.encode("utf-8"),
        signing_input,
        hashlib.sha256,
    ).digest()
    actual_signature = _decode_base64_url(parts[2])
    if not hmac.compare_digest(expected_signature, actual_signature):
        raise ValueError("Invalid bearer token")

    header = json.loads(_decode_base64_url(parts[0]))
    if header.get("alg") != "HS256":
        raise ValueError("Unsupported bearer token")

    claims = json.loads(_decode_base64_url(parts[1]))
    exp = claims.get("exp")
    if exp is not None and int(exp) < int(time.time()):
        raise ValueError("Expired bearer token")

    return claims


def _decode_base64_url(value: str) -> bytes:
    padding = "=" * (-len(value) % 4)
    try:
        return base64.urlsafe_b64decode(value + padding)
    except ValueError as exc:
        raise ValueError("Invalid bearer token") from exc


def _unauthorized(message: str) -> JSONResponse:
    return JSONResponse(
        status_code=401,
        content={"code": "UNAUTHORIZED", "message": message},
    )
