from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
import json

from app.database.dependencies import get_db
from app.utils.current_user import get_current_user
from app.models.resume_model import Resume
from app.models.interview_model import Interview
from app.services.gemini_service import (
    generate_interview_questions,
    generate_feedback
)

router = APIRouter(
    prefix="/interviews",
    tags=["Interviews"]
)


def parse_questions(text: str):
    lines = []

    for line in text.split("\n"):
        line = line.strip()

        if not line:
            continue

        if line[0].isdigit():
            parts = line.split(".", 1)
            if len(parts) == 2:
                line = parts[1].strip()

        lines.append(line)

    questions = []

    for i, q in enumerate(lines):
        questions.append({
            "id": f"q{i + 1}",
            "question": q
        })

    return questions


@router.post("/generate")
def generate_interview(
    payload: dict,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    role = payload.get("role")

    if not role:
        raise HTTPException(
            status_code=400,
            detail="Role is required"
        )

    resume = db.query(Resume).filter(
        Resume.user_id == current_user.id
    ).first()

    if not resume:
        raise HTTPException(
            status_code=404,
            detail="Resume not found"
        )

    raw_questions = generate_interview_questions(
        resume.extracted_text,
        role
    )

    questions = parse_questions(raw_questions)

    interview = Interview(
        user_id=current_user.id,
        role=role,
        questions=json.dumps(questions)
    )

    db.add(interview)
    db.commit()
    db.refresh(interview)

    return {
        "id": interview.id,
        "role": interview.role,
        "questions": questions,
        "submitted": False
    }


@router.get("")
def get_interviews(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    interviews = db.query(Interview).filter(
        Interview.user_id == current_user.id
    ).all()

    result = []

    for i in interviews:
        result.append({
            "id": i.id,
            "role": i.role,
            "questions": json.loads(i.questions),
            "submitted": i.answers is not None,
            "created_at": str(i.id)
        })

    return result


@router.get("/{interview_id}")
def get_interview(
    interview_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    interview = db.query(Interview).filter(
        Interview.id == interview_id,
        Interview.user_id == current_user.id
    ).first()

    if not interview:
        raise HTTPException(
            status_code=404,
            detail="Interview not found"
        )

    return {
        "id": interview.id,
        "role": interview.role,
        "questions": json.loads(interview.questions),
        "submitted": interview.answers is not None
    }


@router.post("/{interview_id}/submit")
def submit_interview(
    interview_id: int,
    payload: dict,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    interview = db.query(Interview).filter(
        Interview.id == interview_id,
        Interview.user_id == current_user.id
    ).first()

    if not interview:
        raise HTTPException(
            status_code=404,
            detail="Interview not found"
        )

    answers = payload.get("answers", [])

    answer_text = "\n".join(
        [
            f"{a['question_id']}: {a['answer']}"
            for a in answers
        ]
    )

    feedback = generate_feedback(
        interview.questions,
        answer_text
    )

    interview.answers = json.dumps(answers)
    interview.feedback = feedback

    db.commit()

    return {
        "message": "Submitted"
    }


@router.get("/{interview_id}/feedback")
def get_feedback(
    interview_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    interview = db.query(Interview).filter(
        Interview.id == interview_id,
        Interview.user_id == current_user.id
    ).first()

    if not interview:
        raise HTTPException(
            status_code=404,
            detail="Interview not found"
        )

    questions = json.loads(interview.questions)

    answers = (
        json.loads(interview.answers)
        if interview.answers
        else []
    )

    items = []

    for idx, q in enumerate(questions):
        answer = answers[idx]["answer"] if idx < len(answers) else ""

        items.append({
            "question_id": q["id"],
            "question": q["question"],
            "answer": answer,
            "score": 70,
            "feedback": interview.feedback or "Feedback generated."
        })

    return {
        "overall_score": 70,
        "items": items
    }