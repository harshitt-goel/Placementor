from fastapi import FastAPI
from app.database.db import engine, Base
from app.models.user_model import User
from app.models.interview_model import Interview
from app.models.profile_model import Profile
from app.routes.resume_routes import router as resume_router
from app.routes.interview_routes import router as interview_router
from app.routes.roadmap_routes import router as roadmap_router
from app.models.progress_model import Progress
from app.routes.progress_routes import router as progress_router

from app.routes.auth_routes import router as auth_router
from app.routes.user_routes import router as user_router
from app.models.resume_model import Resume
from app.routes.profile_routes import router as profile_router
from app.models.roadmap_model import Roadmap
from fastapi.middleware.cors import CORSMiddleware

Base.metadata.create_all(bind=engine)

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)
app.include_router(user_router)
app.include_router(resume_router)
app.include_router(interview_router)
app.include_router(profile_router)
app.include_router(roadmap_router)
app.include_router(progress_router)

@app.get("/")
def home():
    return {
        "message": "AI Interview Platform Backend Running"
    }

