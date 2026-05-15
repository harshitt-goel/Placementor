from fastapi import FastAPI
from app.database.db import engine, Base
from app.models.user_model import User

Base.metadata.create_all(bind=engine)

app = FastAPI()

@app.get("/")
def home():
    return {"message": "AI Interview Platform Backend Running"}