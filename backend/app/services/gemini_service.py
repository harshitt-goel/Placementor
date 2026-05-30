from google import genai
from dotenv import load_dotenv
import os
import json

load_dotenv()

client = genai.Client(
    api_key=os.getenv("GEMINI_API_KEY")
)

def generate_interview_questions(
    resume_text,
    role
):

    prompt = f"""
    You are an expert technical interviewer.

    Based on this resume:

    {resume_text}

    Generate exactly 10 interview questions.

    Rules:
    - Return ONLY the questions.
    - No introduction.
    - No explanation.
    - No heading.
    - No closing statement.
    - Number each question.

    Example:

    1. Question...
    2. Question...
    for the role: {role}

    Include:
    - technical questions
    - project questions
    - behavioral questions

    Return clean numbered questions.
    """

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt
    )

    return response.text


def generate_feedback(
    questions,
    answers
):
    prompt = f"""
    You are an expert technical interviewer.

    Questions:
    {questions}

    Candidate Answers:
    {answers}

    Evaluate EACH answer separately.

    Return ONLY valid JSON.

    {{
    "overall_score": 0,
    "items": [
        {{
        "question_id": "q1",
        "score": 85,
        "feedback": "Good understanding of the concept."
        }}
    ]
    }}

    Rules:
    - overall_score must be between 0 and 100
    - score for each question must be between 0 and 100
    - give short but useful feedback
    - return JSON only
    """

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt
    )
    text = response.text.strip()

    if text.startswith("```json"):
        text = text.replace("```json", "").replace("```", "").strip()

    return json.loads(text)

