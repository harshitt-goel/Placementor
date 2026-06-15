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
    questions = []

    for line in text.split("\n"):
        line = line.strip()

        if not line:
            continue

        if not (
            line.startswith("1.")
            or line.startswith("2.")
            or line.startswith("3.")
            or line.startswith("4.")
            or line.startswith("5.")
            or line.startswith("6.")
            or line.startswith("7.")
            or line.startswith("8.")
            or line.startswith("9.")
            or line.startswith("10.")
        ):
            continue

        q = line.split(".", 1)[1].strip()

        questions.append({
            "id": f"q{len(questions)+1}",
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

    for interview in interviews:
        result.append({
            "id": interview.id,
            "role": interview.role,
            "questions": json.loads(interview.questions),
            "status": interview.status,
            "submitted": interview.answers is not None,
            "created_at": str(interview.id)
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
        "status": interview.status,
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

    feedback_data = generate_feedback(
        interview.questions,
        answer_text
    )

    interview.answers = json.dumps(answers)
    interview.feedback = json.dumps(feedback_data)

    db.commit()

    return {
        "message": "Submitted successfully"
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

    feedback_data = (
        json.loads(interview.feedback)
        if interview.feedback
        else {
            "overall_score": 0,
            "items": []
        }
    )

    gemini_items = {
        item["question_id"]: item
        for item in feedback_data.get("items", [])
    }

    items = []

    for idx, q in enumerate(questions):

        answer = (
            answers[idx]["answer"]
            if idx < len(answers)
            else ""
        )

        gemini_feedback = gemini_items.get(
            q["id"],
            {}
        )

        items.append({
            "question_id": q["id"],
            "question": q["question"],
            "answer": answer,
            "score": gemini_feedback.get("score", 0),
            "feedback": gemini_feedback.get(
                "feedback",
                "No feedback available."
            )
        })

    return {
        "overall_score": feedback_data.get(
            "overall_score",
            0
        ),
        "items": items
    }