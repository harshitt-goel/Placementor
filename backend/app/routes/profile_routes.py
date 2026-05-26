from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database.dependencies import get_db
from app.utils.current_user import get_current_user

from app.models.profile_model import Profile
from app.schemas.profile_schema import ProfileCreate

router = APIRouter(prefix="/profile", tags=["Profile"])


@router.get("/")
def get_profile(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    profile = db.query(Profile).filter(
        Profile.user_id == current_user.id
    ).order_by(Profile.id.desc()).first()

    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")

    return profile


@router.post("/")
def create_profile(
    profile: ProfileCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    existing = db.query(Profile).filter(
        Profile.user_id == current_user.id
    ).first()

    if existing:
        raise HTTPException(status_code=400, detail="Profile already exists")

    new_profile = Profile(
        user_id=current_user.id,
        target_role=profile.target_role,
        domain=profile.domain,
        current_level=profile.current_level,
        github_url=profile.github_url,
        leetcode_url=profile.leetcode_url,
        codeforces_url=profile.codeforces_url,
        target_company=profile.target_company
    )

    db.add(new_profile)
    db.commit()
    db.refresh(new_profile)

    return new_profile


@router.put("/")
def update_profile(
    profile: ProfileCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):
    existing = db.query(Profile).filter(
        Profile.user_id == current_user.id
    ).order_by(Profile.id.desc()).first()

    if not existing:
        raise HTTPException(status_code=404, detail="Profile not found")

    existing.target_role = profile.target_role
    existing.domain = profile.domain
    existing.current_level = profile.current_level
    existing.github_url = profile.github_url
    existing.leetcode_url = profile.leetcode_url
    existing.codeforces_url = profile.codeforces_url
    existing.target_company = profile.target_company

    db.commit()
    db.refresh(existing)

    return existing