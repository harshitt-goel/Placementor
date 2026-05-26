from fastapi import APIRouter, UploadFile, File, Depends
from sqlalchemy.orm import Session
import shutil
import os
from datetime import datetime

from app.database.dependencies import get_db
from app.utils.current_user import get_current_user
from app.utils.pdf_extractor import extract_text_from_pdf
from app.models.resume_model import Resume

router = APIRouter()


@router.post("/upload-resume")
def upload_resume(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    os.makedirs("uploads", exist_ok=True)

    file_path = f"uploads/{file.filename}"

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    extracted_text = extract_text_from_pdf(file_path)

    existing = db.query(Resume).filter(
        Resume.user_id == current_user.id
    ).order_by(Resume.id.desc()).first()

    # UPDATE EXISTING RESUME
    if existing:
        existing.extracted_text = extracted_text

        db.commit()
        db.refresh(existing)

        return {
            "id": existing.id,
            "filename": file.filename,
            "uploaded_at": str(datetime.now()),
            "extracted_text": existing.extracted_text
        }

    # CREATE NEW RESUME
    new_resume = Resume(
        user_id=current_user.id,
        extracted_text=extracted_text
    )

    db.add(new_resume)
    db.commit()
    db.refresh(new_resume)

    return {
        "id": new_resume.id,
        "filename": file.filename,
        "uploaded_at": str(datetime.now()),
        "extracted_text": new_resume.extracted_text
    }


@router.get("/resume")
def get_resume(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    resume = db.query(Resume).filter(
        Resume.user_id == current_user.id
    ).order_by(Resume.id.desc()).first()

    if not resume:
        return None

    return {
        "id": resume.id,
        "filename": "resume.pdf",
        "uploaded_at": str(datetime.now()),
        "extracted_text": resume.extracted_text
    }