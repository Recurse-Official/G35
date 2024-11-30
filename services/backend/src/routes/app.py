import fastapi
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import create_engine
import os
from db.tables import Base, populate_data
from src.utils import get_db_session

from src.routes.user import routes as user_routes
from src.routes.address import routes as address_routes
from src.routes.food import routes as food_routes
from src.routes.orders import routes as order_routes

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_DATABASE = os.getenv("MYSQL_DATABASE")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")

MYSQL_DB_URL = f"mysql+pymysql://{MYSQL_USER}:{MYSQL_PASSWORD}@{MYSQL_HOST}:3306/{MYSQL_DATABASE}"

engine = create_engine(MYSQL_DB_URL)
Base.metadata.create_all(engine)
populate_data(get_db_session())

app = fastapi.FastAPI()

# add cors
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"message": "Hello, World!"}

@app.get("/health")
def health():
    return {"status": "ok"}

app.include_router(user_routes, prefix="/users")
app.include_router(address_routes, prefix="/address")
app.include_router(food_routes, prefix="/food")
app.include_router(order_routes, prefix="/orders")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000, log_level="info")