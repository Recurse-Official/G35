from fastapi import APIRouter
from src.utils import get_db_session, Address
from pydantic import BaseModel
from fastapi import APIRouter
from src.utils import get_db_session, Address
from pydantic import BaseModel
from typing import Optional

# all these are routes under /address
routes = APIRouter()
# session = get_db_session()

class AddressModel(BaseModel):
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

@routes.get("/get/{id}")
def get_address(id: int):
    session = get_db_session()
    address = session.query(Address).where(Address.id == id).first()

    return address

@routes.post("/")
def create_address(address: AddressModel):
    session = get_db_session()
    new_address = Address(**address.model_dump())
    session.add(new_address)
    session.commit()
    session.refresh(new_address)
    session.add(new_address)
    session.commit()
    session.refresh(new_address)

    return {
        "address": new_address,
        "status": "ok"
   }
