from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

import json

from app.database.dependencies import get_db
from app.utils.current_user import get_current_user

from app.models.profile_model import Profile
from app.models.roadmap_model import Roadmap

router = APIRouter(
    prefix="/roadmap",
    tags=["Roadmap"]
)


def format_roadmap(data):

    formatted_phases = []

    for idx, phase in enumerate(
        data.get("phases", []),
        start=1
    ):

        formatted_tasks = []

        for task_idx, task in enumerate(
            phase.get("tasks", []),
            start=1
        ):

            formatted_tasks.append({
                "id": f"phase-{idx}-task-{task_idx}",
                "title": task,
                "description": ""
            })

        formatted_phases.append({
            "phase_number": idx,
            "title": phase.get("phase", ""),
            "description": "",
            "tasks": formatted_tasks
        })

    return {
        "role": data.get("role", ""),
        "phases": formatted_phases
    }


@router.get("/")
def get_roadmap(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
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

    return format_roadmap(
        roadmap_data
    )


@router.post("/generate")
def generate_roadmap(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):

    profile = db.query(Profile).filter(
        Profile.user_id == current_user.id
    ).order_by(Profile.id.desc()).first()

    if not profile:
        raise HTTPException(
            status_code=404,
            detail="Profile not found"
        )

    role = profile.target_role.strip().lower()

    roadmap_files = {
        "backend developer": "app/roadmaps/backend_developer.json",
    }

    if role not in roadmap_files:
        raise HTTPException(
            status_code=400,
            detail=f"Roadmap not available for role: {role}"
        )

    with open(
        roadmap_files[role],
        "r"
    ) as file:

        roadmap_json = json.load(file)

    existing = db.query(Roadmap).filter(
        Roadmap.user_id == current_user.id
    ).first()

    if existing:
        db.delete(existing)
        db.commit()

    new_roadmap = Roadmap(
        user_id=current_user.id,
        role=profile.target_role,
        roadmap_data=json.dumps(roadmap_json)
    )

    db.add(new_roadmap)
    db.commit()

    return format_roadmap(
        roadmap_json
    )