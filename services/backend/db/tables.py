from typing import List, Optional
from sqlalchemy import ForeignKey, String, BigInteger, TIMESTAMP, Float, func
from sqlalchemy.orm import mapped_column
from sqlalchemy.ext.declarative import declarative_base
from geopy.distance import geodesic
from geopy.point import Point
import random

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'
    id = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    email = mapped_column(String(255), nullable=False, unique=True)
    username = mapped_column(String(100), nullable=True)
    full_name = mapped_column(String(50), nullable=True)
    phone = mapped_column(String(15), nullable=True)
    password = mapped_column(String(255), nullable=False)
    created_at = mapped_column(TIMESTAMP, server_default=func.now(), nullable=False)
    updated_at = mapped_column(TIMESTAMP, server_default=func.now(), onupdate=func.now(), nullable=False)
    status = mapped_column(String(50), nullable=True)

    def __repr__(self):
        return (
            f"<User(id={self.id}, email={self.email}, username={self.username}, "
            f"full_name={self.full_name}, phone={self.phone}, "
            f"password={self.password}, created_at={self.created_at}, updated_at={self.updated_at}, "
            f"user_type={self.user_type}, status={self.status})>"
        )

class Address(Base):
    __tablename__ = 'address'
    id = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    address_1 = mapped_column(String(255), nullable=False)
    address_2 = mapped_column(String(255), nullable=True)
    city = mapped_column(String(100), nullable=False)
    state = mapped_column(String(100), nullable=True)
    country = mapped_column(String(100), nullable=False)
    postal_code = mapped_column(String(20), nullable=True)
    latitude = mapped_column(Float, nullable=True)
    longitude = mapped_column(Float, nullable=True)

class Available_Food(Base):
    __tablename__ = 'available_food'
    id = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    user_id = mapped_column(BigInteger, ForeignKey('users.id'), nullable=False)
    food_type = mapped_column(String(100), nullable=False)
    food_title = mapped_column(String(100), nullable=False)
    food_available = mapped_column(String(5000), nullable=False)  # Stringified JSON
    num_servings = mapped_column(BigInteger, nullable=False)
    created_at = mapped_column(TIMESTAMP, server_default=func.now(), nullable=False)
    prepared_date = mapped_column(TIMESTAMP, nullable=True)
    expiration_date = mapped_column(TIMESTAMP, nullable=True)
    status = mapped_column(String(50), nullable=True)
    address_id = mapped_column(BigInteger, ForeignKey('address.id'), nullable=False)

class Food_Orders(Base):
    __tablename__ = 'food_orders'
    id = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    user_id = mapped_column(BigInteger, ForeignKey('users.id'), nullable=False)
    food_available_id = mapped_column(BigInteger, ForeignKey('available_food.id'), nullable=False)
    num_servings = mapped_column(BigInteger, nullable=False)
    created_at = mapped_column(TIMESTAMP, server_default=func.now(), nullable=False)
    status = mapped_column(String(50), nullable=True)
    address_id = mapped_column(BigInteger, ForeignKey('address.id'), nullable=False)
    delivery_address_id = mapped_column(BigInteger, ForeignKey('address.id'), nullable=False)

class User_Statistics(Base):
    __tablename__ = 'user_statistics'
    id = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    user_id = mapped_column(BigInteger, ForeignKey('users.id'), nullable=False)
    total_donations = mapped_column(BigInteger, nullable=False)
    total_orders = mapped_column(BigInteger, nullable=False)
    total_servings_donated = mapped_column(BigInteger, nullable=False)
    total_servings_ordered = mapped_column(BigInteger, nullable=False)

# Base coordinates
BASE_COORDS = (17.453001, 78.394529)

def generate_random_coords(base_coords, min_km=5, max_km=10):
    """Generate random coordinates within the specified distance range."""
    while True:
        # Generate a random bearing and distance
        bearing = random.uniform(0, 360)
        distance = random.uniform(min_km, max_km)
        
        # Generate a new point using geopy
        origin = Point(*base_coords)
        new_point = geodesic(kilometers=distance).destination(origin, bearing)
        
        return new_point.latitude, new_point.longitude

# Populate the tables
def populate_data(session):
    try:
        # Create dummy users
        for i in range(20):
            user = User(
                email=f"user{i}@example.com",
                username=f"user{i}",
                full_name=f"Name{i}",
                phone=f"123456789{i}",
                password="securepassword",
                status="active"
            )
            session.add(user)
            session.commit()
            session.refresh(user)

            user_stats = User_Statistics(
                user_id=user.id,
                total_donations=0,
                total_orders=0,
                total_servings_donated=0,
                total_servings_ordered=0,
            )
            session.add(user_stats)
            session.commit()
            session.refresh(user_stats)

        # Create addresses within 5-10 km of BASE_COORDS
        for i in range(50):
            lat, long = generate_random_coords(BASE_COORDS, min_km=5, max_km=10)
            address = Address(
                address_1=f"Address 1 - {i}",
                address_2=f"Address 2 - {i}",
                city="Hyderabad",
                state="Telangana",
                country="India",
                postal_code=f"50000{i}",
                latitude=lat,
                longitude=long,
            )
            session.add(address)
        session.commit()

        # Create available food entries
        users = session.query(User).all()
        addresses = session.query(Address).all()
        for user in users:
            for i in range(2):  # Each user has 3 food items available
                food = Available_Food(
                    user_id=user.id,
                    food_type=f"Food Type {i}",
                    food_title=f"Food Title {i}",
                    food_available=f'{{"item{i}": "Food Item {i}"}}',
                    num_servings=random.randint(5, 20),
                    prepared_date=func.now(),
                    expiration_date=func.now(),
                    status="available",
                    address_id=random.choice(addresses).id,
                )
                session.add(food)
        session.commit()

        print("Data populated successfully!")

    except Exception as e:
        session.rollback()
        print("An error occurred:", e)
    finally:
        session.close()
