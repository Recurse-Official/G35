from fastapi import APIRouter, Depends, HTTPException
from src.utils import get_db_session, User, Session, User_Statistics, Food_Orders, Available_Food
from pydantic import BaseModel, EmailStr
from typing import Optional

# all these are routes under /users
routes = APIRouter()
# session = get_db_session()

class UserCreate(BaseModel):
    email: EmailStr
    username: Optional[str]
    full_name: Optional[str]
    phone: Optional[str]
    password: str
    status: Optional[str]

@routes.get("/")
def root():
    return {"message": "Hello, World!"}

@routes.get("/login")
def login_user(email: str, password: str):
    try:
        session = get_db_session()
        user = session.query(User).where(User.email == email).first()

        if not user:
            raise HTTPException(status_code=400, detail="User not found")
        
        if user.password != password:
            raise HTTPException(status_code=400, detail="Incorrect password")
        
        return {
            "user": user,
            "status": "ok"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@routes.post("/signup")
def signup_user(user: UserCreate):
    try:
        session = get_db_session()
        if session.query(User).where(User.email == user.email).first():
            raise HTTPException(status_code=400, detail="Email already registered")
        
        new_user = User(**user.model_dump())
        session.add(new_user)
        session.commit()
        session.refresh(new_user)

        user_stats = User_Statistics(
            user_id=new_user.id,
            total_donations=0,
            total_orders=0,
            total_servings_donated=0,
            total_servings_ordered=0,
        )
        session.add(user_stats)
        session.commit()
        session.refresh(user_stats)

        return {
            "user": new_user,
            "status": "ok"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    
@routes.get("/stats/")
def get_user_stats(user_id: int):
    session = get_db_session()
    stats = session.query(User_Statistics).where(User_Statistics.user_id == user_id).first()

    return stats

@routes.get("/id/")
def get_user_id(email: str):
    session = get_db_session()
    user = session.query(User).where(User.email == email).first()

    return user

@routes.get("/get/orders/")
def get_user_orders(user_id: int):
    session = get_db_session()
    orders = session.query(
        Food_Orders,
        Available_Food
    ).join(
        Available_Food,
        Food_Orders.food_available_id == Available_Food.id
    ).where(
        Food_Orders.user_id == user_id
    ).all()

    return [
        {
            "order": order[0],
            "food": order[1]
        }
        for order in orders
    ]

@routes.get("/get/donations/")
def get_user_donations(user_id: int):
    session = get_db_session()
    donations = session.query(Available_Food).where(Available_Food.user_id == user_id).all()

    return donations

@routes.get("/leaderboard")
def get_leaderboard():
    # get the top 5 users with the highest total servings donated
    session = get_db_session()
    users = session.query(User.full_name, User_Statistics.total_servings_donated).join(User_Statistics, User.id == User_Statistics.user_id).order_by(User_Statistics.total_servings_donated.desc()).limit(5).all()

    return [
        {
            "name": user[0],
            "plates": user[1]
        }
        for user in users
    ]