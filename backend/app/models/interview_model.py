from sqlalchemy import (
    Column,
    Integer,
    String,
    Text,
    ForeignKey
)

from app.database.db import Base

class Interview(Base):

    __tablename__ = "interviews"

    id = Column(Integer, primary_key=True)

    user_id = Column(
        Integer,
        ForeignKey("users.id")
    )

    role = Column(String)

    questions = Column(Text)
    status = Column(
        String,
        default="COMPLETED"
    )
    answers = Column(Text, nullable=True)
    feedback = Column(Text, nullable=True)
    score = Column(Integer, nullable=True)