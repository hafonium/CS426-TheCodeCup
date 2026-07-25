from fastapi import APIRouter
from app.api.v1.endpoints import user_endpoint, auth_endpoint, food_endpoint, food_option_endpoint, cart_endpoint, order_endpoint

api_router = APIRouter()
api_router.include_router(user_endpoint.router)
api_router.include_router(auth_endpoint.router)
api_router.include_router(food_endpoint.router)
api_router.include_router(food_option_endpoint.router)
api_router.include_router(cart_endpoint.router)
api_router.include_router(order_endpoint.router)