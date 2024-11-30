from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os
from db.tables import Base, User, Address, Available_Food, Food_Orders, User_Statistics

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_DATABASE = os.getenv("MYSQL_DATABASE")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")

MYSQL_DB_URL = f"mysql+pymysql://{MYSQL_USER}:{MYSQL_PASSWORD}@{MYSQL_HOST}:3306/{MYSQL_DATABASE}"

engine = create_engine(MYSQL_DB_URL)
Session = sessionmaker(bind=engine, autoflush=False, autocommit=False)

def get_db_session():
    session = Session()
    return session
