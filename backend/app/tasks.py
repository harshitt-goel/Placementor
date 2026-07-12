import json
from app.database.db import SessionLocal
from app.models.user_model import User
from app.models.interview_model import Interview
from app.models.profile_model import Profile
from app.models.resume_model import Resume
from app.models.roadmap_model import Roadmap
from app.models.progress_model import Progress
from app.services.gemini_service import generate_interview_questions, generate_feedback

def parse_questions(text: str):
    questions = []
    for line in text.split("\n"):
        line = line.strip()
        if not line:
            continue
        if not (
            line.startswith("1.") or line.startswith("2.") or line.startswith("3.") or line.startswith("4.") or
            line.startswith("5.") or line.startswith("6.") or line.startswith("7.") or line.startswith("8.") or
            line.startswith("9.") or line.startswith("10.")
        ):
            continue
        q = line.split(".", 1)[1].strip()
        questions.append({
            "id": f"q{len(questions)+1}",
            "question": q
        })
    return questions

def generate_questions_task(interview_id: int, resume_text: str, role: str):
    db = SessionLocal()
    try:
        raw_questions = generate_interview_questions(resume_text, role)
        questions = parse_questions(raw_questions)
        
        interview = db.query(Interview).filter(Interview.id == interview_id).first()
        if interview:
            interview.questions = json.dumps(questions)
            interview.status = "COMPLETED"
            db.commit()
    except Exception as e:
        db.rollback()
        interview = db.query(Interview).filter(Interview.id == interview_id).first()
        if interview:
            interview.status = "FAILED"
            db.commit()
        raise e
    finally:
        db.close()

def generate_feedback_task(interview_id: int, questions_json: str, answer_text: str, answers_json: str):
    db = SessionLocal()
    try:
        feedback_data = generate_feedback(questions_json, answer_text)
        
        interview = db.query(Interview).filter(Interview.id == interview_id).first()
        if interview:
            interview.answers = answers_json
            interview.feedback = json.dumps(feedback_data)
            interview.status = "COMPLETED"
            db.commit()
    except Exception as e:
        db.rollback()
        interview = db.query(Interview).filter(Interview.id == interview_id).first()
        if interview:
            interview.status = "FAILED"
            db.commit()
        raise e
    finally:
        db.close()
