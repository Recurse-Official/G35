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
    num_servings_left = mapped_column(BigInteger, nullable=False)
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


import random
from datetime import datetime

# Example lists for more realistic data
names = ["John Doe", "Jane Smith", "Michael Johnson", "Emily Davis", "William Brown", "Olivia Wilson", 
         "James Taylor", "Sophia Moore", "Benjamin Harris", "Mia Clark", "Lucas Lewis", "Amelia Young", 
         "David Martinez", "Isabella Walker", "Matthew Allen", "Chloe King", "Alexander Scott", "Charlotte Wright"]

cities = ["Hyderabad", "Bangalore", "Mumbai", "Delhi", "Chennai", "Kolkata", "Pune", "Ahmedabad", "Jaipur", "Chandigarh"]
states = ["Telangana", "Maharashtra", "Uttar Pradesh", "Karnataka", "Gujarat", "Rajasthan", "Punjab", "West Bengal"]
countries = ["India"]

food_titles = ["Biryani", "Pizza", "Pasta", "Sandwich", "Tacos", "Burger", "Fried Rice", "Grilled Chicken", 
               "Veg Salad", "Vegetable Curry", "Cheesecake", "Sushi", "Pani Puri", "Samosa", "Chole Bhature"]
food_types = ["Veg", "Non-Veg"]
food_descriptions = ["Spicy and delicious", "Freshly prepared", "Delicious and healthy", "Best served hot", "Tasty and filling"]

phone_numbers = ["1234567890", "9876543210", "1029384756", "5647382910", "9081726354", "1230984567", "9817364502"]

# Base coordinates for generating random addresses
BASE_COORDS = (17.453001, 78.394529)

# Function to generate random coordinates
def generate_random_coords(base_coords, min_km=5, max_km=10):
    while True:
        bearing = random.uniform(0, 360)
        distance = random.uniform(min_km, max_km)
        origin = Point(*base_coords)
        new_point = geodesic(kilometers=distance).destination(origin, bearing)
        return new_point.latitude, new_point.longitude

# Populate the tables with realistic data
def populate_data(session):
    try:
        # Create dummy users with more realistic data
        for i in range(20):
            user = User(
                email=f"user{i}@example.com",
                username=f"user{i}",
                full_name=random.choice(names),
                phone=random.choice(phone_numbers),
                password="securepassword",
                status="active"
            )
            session.add(user)
            session.commit()
            session.refresh(user)

            user_stats = User_Statistics(
                user_id=user.id,
                total_donations=random.randint(0, 50),
                total_orders=random.randint(0, 50),
                total_servings_donated=random.randint(0, 500),
                total_servings_ordered=random.randint(0, 500),
            )
            session.add(user_stats)
            session.commit()
            session.refresh(user_stats)

        # Create addresses with realistic city and state data, within 5-10 km of base coordinates
        for i in range(50):
            lat, long = generate_random_coords(BASE_COORDS, min_km=5, max_km=10)
            address = Address(
                address_1=f"Street {random.randint(1, 100)}",
                address_2=f"Apt {random.randint(1, 20)}",
                city=random.choice(cities),
                state=random.choice(states),
                country=random.choice(countries),
                postal_code=f"{random.randint(100000, 999999)}",
                latitude=lat,
                longitude=long,
            )
            session.add(address)
        session.commit()

        # Create available food entries with more realistic food data
        users = session.query(User).all()
        addresses = session.query(Address).all()
        for user in users:
            for i in range(3):  # Each user has 3 food items available
                food = Available_Food(
                    user_id=user.id,
                    food_type=random.choice(food_types),
                    food_title=random.choice(food_titles),
                    food_available=random.choice(food_descriptions),
                    num_servings=random.randint(5, 20),
                    num_servings_left=random.randint(2, 10),
                    prepared_date=datetime.now(),
                    expiration_date=datetime.now(),
                    status="available",
                    address_id=random.choice(addresses).id,
                )
                session.add(food)
        session.commit()

        # Create food orders (link users to available food entries)
        for i in range(100):  # Create 100 random food orders
            user = random.choice(users)
            available_food = random.choice(session.query(Available_Food).all())
            delivery_address = random.choice(addresses)

            order = Food_Orders(
                user_id=user.id,
                food_available_id=available_food.id,
                num_servings=random.randint(1, available_food.num_servings_left),
                status="pending",
                created_at=datetime.now(),
                address_id=random.choice(addresses).id,
                delivery_address_id=delivery_address.id
            )
            session.add(order)
        session.commit()

        print("Data populated successfully!")

    except Exception as e:
        session.rollback()
        print("An error occurred:", e)
    finally:
        session.close()


# # Populate the tables
# def populate_data(session):
#     try:
#         # Create dummy users
#         for i in range(20):
#             user = User(
#                 email=f"user{i}@example.com",
#                 username=f"user{i}",
#                 full_name=f"Name{i}",
#                 phone=f"123456789{i}",
#                 password="securepassword",
#                 status="active"
#             )
#             session.add(user)
#             session.commit()
#             session.refresh(user)

#             user_stats = User_Statistics(
#                 user_id=user.id,
#                 total_donations=0,
#                 total_orders=0,
#                 total_servings_donated=0,
#                 total_servings_ordered=0,
#             )
#             session.add(user_stats)
#             session.commit()
#             session.refresh(user_stats)

#         # Create addresses within 5-10 km of BASE_COORDS
#         for i in range(50):
#             lat, long = generate_random_coords(BASE_COORDS, min_km=5, max_km=10)
#             address = Address(
#                 address_1=f"Address 1 - {i}",
#                 address_2=f"Address 2 - {i}",
#                 city="Hyderabad",
#                 state="Telangana",
#                 country="India",
#                 postal_code=f"50000{i}",
#                 latitude=lat,
#                 longitude=long,
#             )
#             session.add(address)
#         session.commit()

#         # Create available food entries
#         users = session.query(User).all()
#         addresses = session.query(Address).all()
#         for user in users:
#             for i in range(2):  # Each user has 3 food items available
#                 food = Available_Food(
#                     user_id=user.id,
#                     food_type=random.choice(["Veg", "Non-Veg"]),
#                     food_title=f"Food Title {i}",
#                     food_available=f'Food description {i}',
#                     num_servings=random.randint(5, 20),
#                     num_servings_left=random.randint(2, 10),
#                     prepared_date=func.now(),
#                     expiration_date=func.now(),
#                     status="available",
#                     address_id=random.choice(addresses).id,
#                 )
#                 session.add(food)
#         session.commit()

#         print("Data populated successfully!")

#     except Exception as e:
#         session.rollback()
#         print("An error occurred:", e)
#     finally:
#         session.close()
