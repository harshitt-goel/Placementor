from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

from app.database.dependencies import get_db
from app.models.user_model import User
from app.utils.auth import verify_token

oauth2_scheme = OAuth2PasswordBearer(
    tokenUrl="login"
)

def get_current_user(
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db)
):

    payload = verify_token(token)

    if not payload:
        raise HTTPException(
        status_code=401,
        detail="Invalid token"
        )

    email = payload.get("sub")

    if not email:
        raise HTTPException(
            status_code=401,
            detail="Invalid token"
        )

    user = db.query(User).filter(
        User.email == email
    ).first()

    if not user:
        raise HTTPException(
            status_code=401,
            detail="User not found"
        )

    return user