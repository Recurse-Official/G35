from fastapi import APIRouter
from src.utils import get_db_session, Food_Orders, Available_Food, User_Statistics
from pydantic import BaseModel
from typing import Optional
from src.routes.address import create_address, AddressModel

# all these are routes under /orders
routes = APIRouter()
# session = get_db_session()

class OrderModel(BaseModel):
    user_id: int
    food_available_id: int
    num_servings: int
    address_id: int
    delivery_address_id: int
    status: Optional[str]

class AllOrderModel(BaseModel):
    user_id: int
    food_available_id: int
    num_servings: int
    address_id: int
    status: Optional[str]
    address_1: str
    address_2: Optional[str]
    city: str
    state: str
    country: str
    postal_code: str
    latitude: Optional[float]
    longitude: Optional[float]

@routes.get("/")
def root():
    return {"message": "Hello, World!"}

@routes.post("/")
def create_order(order: OrderModel):
    try:
        session = get_db_session()
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
        if food.num_servings_left < order.num_servings:
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
        food.num_servings_left -= order.num_servings
        if food.num_servings_left == 0:
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

@routes.post("/create")
def create_all_order(all_order: AllOrderModel):
    try:
        address = AddressModel(
            address_1=all_order.address_1,
            address_2=all_order.address_2,
            city=all_order.city,
            state=all_order.state,
            country=all_order.country,
            postal_code=all_order.postal_code,
            latitude=all_order.latitude or 17.453001,
            longitude=all_order.longitude or 78.394529
        )

        address_id = create_address(address)["address"].id

        order = OrderModel(
            user_id=all_order.user_id,
            food_available_id=all_order.food_available_id,
            num_servings=all_order.num_servings,
            address_id=all_order.address_id,
            delivery_address_id=address_id,
            status=all_order.status
        )

        return create_order(order)
    
    except Exception as e:
        return {
            "status": "error",
            "message": str(e)
        }
