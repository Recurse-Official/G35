from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy import func, and_
from src.utils import get_db_session, Available_Food, Session, Address, User_Statistics, get_coords_from_address
from pydantic import BaseModel, EmailStr
from typing import Optional
import logging
from src.routes.address import create_address, AddressModel
from src.routes.orders import create_order, OrderModel
from datetime import datetime

# all these are routes under /food
routes = APIRouter()
# session = get_db_session()

class FoodModel(BaseModel):
    user_id: int
    food_type: str # Veg / Non-Veg
    food_title: str
    food_available: str
    num_servings: int
    prepared_date: str
    expiration_date: str
    address_1: str
    address_2: Optional[str]
    city: str
    state: str
    country: str
    postal_code: str
    latitude: Optional[float]
    longitude: Optional[float]

class ImmediateModel(BaseModel):
    user_id: int
    address_1: str
    address_2: Optional[str]
    city: str
    state: str
    country: str
    postal_code: str
    num_servings: int
    latitude: Optional[float]
    longitude: Optional[float]

@routes.get("/")
def root():
    return {"message": "Hello, World!"}

@routes.get("/nearby")
def get_near_food(
    lat: float = 17.3967144,
    long: float = 78.4898198,
    radius_km: int = 1000
):
    session = get_db_session()
    distance_formula = (
        6371 * func.acos(
            func.cos(func.radians(lat)) * func.cos(func.radians(Address.latitude)) *
            func.cos(func.radians(Address.longitude) - func.radians(long)) +
            func.sin(func.radians(lat)) * func.sin(func.radians(Address.latitude))
        )
    )

    # ORM Query to filter nearby available food 
    nearby_food = (
        session.query(
            Available_Food.id,
            Available_Food.user_id,
            Available_Food.food_title,
            Available_Food.food_available,
            Available_Food.num_servings_left,
            Available_Food.prepared_date,
            Available_Food.expiration_date,
            Available_Food.status,
            distance_formula.label("distance")
        )
        .join(Address, Available_Food.address_id == Address.id)  # Apply the join condition
        .filter(
            and_(
                distance_formula <= radius_km,
                Available_Food.status == "available"
            )
        )
        .order_by("expiration_date")
        .order_by("distance")
        .all()
    )

    return [
        {
            "id": food.id,
            "user_id": food.user_id,
            "food_title": food.food_title,
            "food_available": food.food_available,
            "num_servings": food.num_servings_left,
            "prepared_date": food.prepared_date.strftime("%B %d, %Y"),
            "expiration_date": food.expiration_date.strftime("%B %d, %Y"),
            "status": food.status,
            "distance": food.distance,
        }
        for food in nearby_food
    ]

@routes.post("/add")
def add_food(
    food_model: FoodModel
):
    session = get_db_session()
    try:
        address = AddressModel(
            address_1=food_model.address_1,
            address_2=food_model.address_2,
            city=food_model.city,
            state=food_model.state,
            country=food_model.country,
            postal_code=food_model.postal_code,
            latitude=food_model.latitude,
            longitude=food_model.longitude
        )
        address_id = create_address(address)["address"].id

        new_food = Available_Food(
            user_id=food_model.user_id,
            food_type=food_model.food_type,
            food_title=food_model.food_title,
            food_available=food_model.food_available,
            num_servings=food_model.num_servings,
            num_servings_left=food_model.num_servings,
            prepared_date=food_model.prepared_date,
            expiration_date=food_model.expiration_date,
            status="available",
            address_id=address_id
        )
        session.add(new_food)
        session.commit()
        session.refresh(new_food)

        user_stats = session.query(User_Statistics).where(User_Statistics.user_id == food_model.user_id).first()
        user_stats.total_donations += 1
        user_stats.total_servings_donated += food_model.num_servings
        session.add(user_stats)
        session.commit()
        session.refresh(user_stats)

        return {
            "food": new_food,
            "status": "ok"
        }
    except Exception as e:
        session.rollback()
        logging.error(f"An error occurred: {e}")
        return {
            "status": "error",
            "message": str(e)
        }

@routes.get("/get/")
def get_food(id: int):
    session = get_db_session()
    food = session.query(Available_Food).where(Available_Food.id == id).first()

    return food

@routes.post("/immediate")
def get_immediate(immediate: ImmediateModel):
    session = get_db_session()

    lat = immediate.latitude
    long = immediate.longitude

    immediate_food = (
        session.query(
            Available_Food.id,
            Available_Food.num_servings_left,
            Available_Food.prepared_date,
            Available_Food.expiration_date,
            Available_Food.status,
            Available_Food.address_id,
            func.acos(
                func.cos(func.radians(lat)) * func.cos(func.radians(Address.latitude)) *
                func.cos(func.radians(Address.longitude) - func.radians(long)) +
                func.sin(func.radians(lat)) * func.sin(func.radians(Address.latitude))
            ).label("distance")
        )
        .join(Address, Available_Food.address_id == Address.id)
        .filter(
            and_(
                Available_Food.status == "available",
                Available_Food.num_servings_left >= immediate.num_servings
            )
        )
        .order_by("distance")
        .limit(1)
        .all()
    )

    address = AddressModel(
        address_1=immediate.address_1,
        address_2=immediate.address_2,
        city=immediate.city,
        state=immediate.state,
        country=immediate.country,
        postal_code=immediate.postal_code,
        latitude=immediate.latitude,
        longitude=immediate.longitude
    )
    address_id = create_address(address)["address"].id

    order = OrderModel(
        user_id=immediate.user_id,
        food_available_id=immediate_food[0].id,
        num_servings=immediate.num_servings,
        address_id=immediate_food[0].address_id,
        delivery_address_id=address_id,
        status="pending"
    )

    return create_order(order)
    