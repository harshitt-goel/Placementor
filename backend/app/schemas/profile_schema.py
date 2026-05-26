from pydantic import BaseModel, HttpUrl
from typing import Optional
class ProfileCreate(BaseModel):

    target_role: str
    domain: str
    current_level: str


    

    github_url: Optional[str] = None
    leetcode_url: Optional[str] = None
    codeforces_url: Optional[str] = None
    target_company: Optional[str] = None