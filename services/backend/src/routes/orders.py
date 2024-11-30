from fastapi import APIRouter
from src.utils import get_db_session, Food_Orders, Available_Food, User_Statistics
from pydantic import BaseModel
from typing import Optional

# all these are routes under /orders
routes = APIRouter()
session = get_db_session()

class OrderModel(BaseModel):
    user_id: int
    food_available_id: int
    num_servings: int
    address_id: int
    delivery_address_id: int
    status: Optional[str]

@routes.get("/")
def root():
    return {"message": "Hello, World!"}

@routes.post("/")
def create_order(order: OrderModel):
    try:
        if order.num_servings <= 0:
            return {
                "status": "error",
                "message": "Number of servings must be greater than 0"
            }
        # Fetch the available food entry
        food = session.query(Available_Food).where(Available_Food.id == order.food_available_id).first()
        if not food:
            return {
                "status": "error",
                "message": "Food item not found"
            }

        # Check if there are enough servings
        if food.num_servings < order.num_servings:
            return {
                "status": "error",
                "message": "Not enough servings available"
            }

        # Create a new order
        new_order = Food_Orders(**order.model_dump())
        session.add(new_order)
        session.commit()
        session.refresh(new_order)

        user_stats = session.query(User_Statistics).where(User_Statistics.user_id == order.user_id).first()
        user_stats.total_orders += 1
        user_stats.total_servings_ordered += order.num_servings
        session.add(user_stats)
        session.commit()
        session.refresh(user_stats)

        # Update the number of servings and status
        food.num_servings -= order.num_servings
        if food.num_servings == 0:
            food.status = "completed"
        session.add(food)
        session.commit()
        session.refresh(food)

        return {
            "order": new_order,
            "status": "ok"
        }

    except Exception as e:
        session.rollback()
        return {
            "status": "error",
            "message": str(e)
        }
