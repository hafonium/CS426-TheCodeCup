from fastapi import APIRouter
from app.api.v1.endpoints import user_endpoint, auth_endpoint

api_router = APIRouter()
api_router.include_router(user_endpoint.router)
api_router.include_router(auth_endpoint.router)