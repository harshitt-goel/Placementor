from fastapi import FastAPI
from app.database.db import engine, Base
from app.models.user_model import User

from app.routes.auth_routes import router as auth_router
from app.routes.user_routes import router as user_router

Base.metadata.create_all(bind=engine)

app = FastAPI()

app.include_router(auth_router)
app.include_router(user_router)

@app.get("/")
def home():
    return {
        "message": "AI Interview Platform Backend Running"
    }