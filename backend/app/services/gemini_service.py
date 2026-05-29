from google import genai
from dotenv import load_dotenv
import os

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

    Generate 10 interview questions
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

    Interview Questions:
    {questions}

    Candidate Answers:
    {answers}

    Evaluate the candidate.

    Provide:
    - strengths
    - weaknesses
    - technical evaluation
    - communication evaluation
    - improvement suggestions

    Also provide a score out of 100.
    """

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt
    )

    return response.text