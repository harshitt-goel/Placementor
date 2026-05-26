from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel

import json

from app.database.dependencies import get_db
from app.utils.current_user import get_current_user

from app.models.roadmap_model import Roadmap
from app.models.progress_model import Progress

router = APIRouter(prefix="/progress", tags=["Progress"])


class TaskCompleteRequest(BaseModel):
    task_name: str


@router.post("/complete")
def complete_task(
    request: TaskCompleteRequest,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_user)
):

    roadmap = db.query(Roadmap).filter(
        Roadmap.user_id == current_user.id
    ).order_by(Roadmap.id.desc()).first()

    if not roadmap:
        raise HTTPException(
            status_code=404,
            detail="Roadmap not found"
        )

    existing_task = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.task_name == request.task_name
    ).first()

    if existing_task:
        existing_task.completed = True

    else:
        new_progress = Progress(
            user_id=current_user.id,
            role=roadmap.role,
            task_name=request.task_name,
            completed=True
        )

        db.add(new_progress)

    db.commit()

    return {
        "message": "Task marked as completed"
    }


@router.get("/dashboard")
def progress_dashboard(
    db: Session = Depends(get_db),
    current_user = Depends(get_current_user)
):

    roadmap = db.query(Roadmap).filter(
        Roadmap.user_id == current_user.id
    ).order_by(Roadmap.id.desc()).first()

    if not roadmap:
        raise HTTPException(
            status_code=404,
            detail="Roadmap not found"
        )

    roadmap_data = json.loads(
        roadmap.roadmap_data
    )

    print(roadmap_data)

    phases = roadmap_data.get("phases", [])

    total_tasks = 0

    for phase in phases:
        total_tasks += len(
            phase.get("tasks", [])
        )

    completed_tasks = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.completed == True
    ).count()

    percentage = (
        (completed_tasks / total_tasks) * 100
        if total_tasks > 0 else 0
    )

    return {
        "role": roadmap.role,
        "total_tasks": total_tasks,
        "completed_tasks": completed_tasks,
        "progress_percentage": round(percentage, 2)
    }

@router.post("/uncomplete")
def uncomplete_task(
    request: TaskCompleteRequest,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_user)
):

    task = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.task_name == request.task_name
    ).first()

    if not task:
        raise HTTPException(
            status_code=404,
            detail="Task not found"
        )

    task.completed = False

    db.commit()

    return {
        "message": "Task marked as incomplete"
    }

@router.get("/")
def get_progress(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    roadmap = db.query(Roadmap).filter(
        Roadmap.user_id == current_user.id
    ).order_by(Roadmap.id.desc()).first()

    if not roadmap:
        return None

    roadmap_data = json.loads(roadmap.roadmap_data)

    phases = roadmap_data.get("phases", [])

    completed = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.completed == True
    ).all()

    completed_task_names = [
        task.task_name for task in completed
    ]

    total_tasks = 0

    for phase in phases:
        total_tasks += len(phase.get("tasks", []))

    completed_tasks = len(completed_task_names)

    percentage = (
        round((completed_tasks / total_tasks) * 100)
        if total_tasks > 0 else 0
    )

    return {
        "completed_task_ids": completed_task_names,
        "completed_tasks": completed_tasks,
        "total_tasks": total_tasks,
        "percentage": percentage
    }