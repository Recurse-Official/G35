from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os
from db.tables import Base, User, Address, Available_Food, Food_Orders, User_Statistics
import requests

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_DATABASE = os.getenv("MYSQL_DATABASE")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")

MYSQL_DB_URL = f"mysql+pymysql://{MYSQL_USER}:{MYSQL_PASSWORD}@{MYSQL_HOST}:3306/{MYSQL_DATABASE}"

OLA_MAPS_URL = "https://api.olamaps.io"
OLA_MAPS_API_KEY = os.getenv("OLA_MAPS_API_KEY")

engine = create_engine(MYSQL_DB_URL)
Session = sessionmaker(bind=engine, autoflush=False, autocommit=False)

def get_db_session():
    session = Session()
    return session

def get_coords_from_address(address: str):
    url = f"{OLA_MAPS_URL}/places/v1/geocode?address={address}?api_key={OLA_MAPS_API_KEY}"
    headers = {
        "Authorization": f"Bearer {OLA_MAPS_API_KEY}",
        "Accept": "application/json"
    }
    response = requests.get(url, headers=headers)
    data = response.json()
    print(data)
    return data
    coords = data["geocodingResults"][0]["geometry"]["location"]
    return (coords["lat"], coords["lng"])