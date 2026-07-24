from fastapi import FastAPI, Depends
from app.core.config import settings
from app.api.router import api_router
from app.models import Base
from app.core.database import engine
from fastapi.security import OAuth2PasswordBearer

# Automatically generate the SQLite database tables on startup
Base.metadata.create_all(bind=engine)

app = FastAPI(title=settings.PROJECT_NAME)

# Attach the master router to the app
app.include_router(api_router, prefix="/api/v1")

@app.get("/")
def read_root():
    return {"Hello": "from backend!"}