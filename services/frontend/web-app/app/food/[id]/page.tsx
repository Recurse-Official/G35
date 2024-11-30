"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "@/constants";
import { use } from "react";
import "./styles.css"; // Import the CSS for styling

type FoodPageProps = {
  params: Promise<{
    id: string;
  }>;
};

interface FoodDetails {
  id: number;
  food_type: string;
  food_title: string;
  food_available: string;
  num_servings: number;
  user_id: number;
  status: string;
  prepared_date: string;
  created_at: string;
  expiration_date: string;
  address_id: number;
}

interface AddressDetails {
  id: number;
  address_1: string;
  address_2: string;
  city: string;
  state: string;
  country: string;
  postal_code: string;
  latitude: number;
  longitude: number;
}

// interface OrderDetails {
//   user_id: number;
//   food_available_id: number;
//   num_servings: number;
//   address_id: number;
//   delivery_address_id: number;
//   status: string;
// }

export default function FoodPage({ params }: FoodPageProps) {
  const { id } = use(params);
  const [foodDetails, setFoodDetails] = useState({} as FoodDetails);
  const [addressId, setAddressId] = useState(0);
  const [addressDetails, setAddressDetails] = useState({} as AddressDetails);
  const [orderPlacing, setOrderPlacing] = useState(false);

  const [numServings, setNumServings] = useState(1);
  const [deliveryAddress, setDeliveryAddress] = useState({
    address_1: "",
    address_2: "",
    city: "",
    state: "",
    country: "",
    postal_code: "",
  });
  const [deliveryAddressId, setDeliveryAddressId] = useState(0);
  const [coordinates, setCoordinates] = useState({ latitude: 0, longitude: 0 });

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoordinates({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        });
      },
      (error) => console.error("Error getting location:", error)
    );
  }
    , []);

  useEffect(() => {
    axios.get(`${BACKEND_URL}/food/get/${id}`)
      .then((response: {data: FoodDetails}) => {
        setFoodDetails(response.data);
        setAddressId(response.data.address_id);
      })
      .catch((error: Error) => console.error("Error fetching food data:", error));
  }, [id]);

  useEffect(() => {
    if (addressId) {
      axios.get(`${BACKEND_URL}/address/get/${addressId}`)
        .then((response: {data: AddressDetails}) => setAddressDetails(response.data))
        .catch((error: Error) => console.error("Error fetching address data:", error));
    }
  }, [addressId]);

  if (!foodDetails || !addressDetails) {
    return <div className="loading">Loading...</div>;
  }

  function foodTypeLogo() {
    if (foodDetails.food_type === "Veg") {
      return <img src="/images/veg.png" alt="Vegetarian" className="food-type-logo" />;
    } else if (foodDetails.food_type === "Non-Veg") {
      return <img src="/images/non-veg.png" alt="Non-Vegetarian" className="food-type-logo" />;
    }
  }

  const handleGetItClick = () => {
    setOrderPlacing(true);
  }

  const handleOrder = () => {
    console.log("Order placing");

    // post delivery address and get its id
    axios.post(`${BACKEND_URL}/address`, {
      address_1: deliveryAddress.address_1,
      address_2: deliveryAddress.address_2,
      city: deliveryAddress.city,
      state: deliveryAddress.state,
      country: deliveryAddress.country,
      postal_code: deliveryAddress.postal_code,
      latitude: coordinates.latitude,
      longitude: coordinates.longitude,
    })
    .then((response: {data: {address: {id: number}}}) => {
      console.log("Delivery Address Submitted:", response.data);
      setDeliveryAddressId(response.data.address.id);
      // place order
      axios.post(`${BACKEND_URL}/orders`, {
        user_id: foodDetails.user_id,
        food_available_id: foodDetails.id,
        num_servings: numServings,
        address_id: foodDetails.address_id,
        delivery_address_id: deliveryAddressId,
        status: "Pending",
      })
      .then((response: {data: object}) => {
        console.log("Order Submitted:", response.data);
        setOrderPlacing(false);
      })
      .catch((error: Error) => console.error("Error placing order:", error));
    })
    .catch((error: Error) => console.error("Error submitting delivery address:", error));

    window.location.href = "/home";
  };

  return (
    <div className="food-page-container">
      {/* Top Image Section */}
      <div className="food-image-container">
        <img
          src={"https://i.ytimg.com/vi/oskLFFcvhGI/maxresdefault.jpg"} // Placeholder if no image URL
          alt={foodDetails.food_title}
          className="food-image"
        />
      </div>

      {/* Food Details Section */}
      <div className="food-details">
        <h1 className="food-title">
          {foodDetails.food_title}
          {foodTypeLogo()}
        </h1>
        <p className="food-detail">Description: {foodDetails.food_available}</p>
        <p className="food-detail">Servings: {foodDetails.num_servings}</p>
        <p className="food-detail">
          Prepared Date: {new Date(foodDetails.prepared_date).toLocaleDateString()}
        </p>
        <p className="food-detail">
          Expiration Date: {new Date(foodDetails.expiration_date).toLocaleDateString()}
        </p>
        <p className="food-detail">Address ID: {foodDetails.address_id}</p>
      </div>

      {/* Address Details Section */}
      <div className="address-details">
        <h2>Address Details</h2>
        <div className="address-detail-row">
          <span className="address-label">Address Line 1:</span>
          <span className="address-value">{addressDetails.address_1}</span>
        </div>
        {addressDetails.address_2 && (
          <div className="address-detail-row">
            <span className="address-label">Address Line 2:</span>
            <span className="address-value">{addressDetails.address_2}</span>
          </div>
        )}
        <div className="address-detail-row">
          <span className="address-label">City:</span>
          <span className="address-value">{addressDetails.city}</span>
        </div>
        <div className="address-detail-row">
          <span className="address-label">State:</span>
          <span className="address-value">{addressDetails.state}</span>
        </div>
        <div className="address-detail-row">
          <span className="address-label">Country:</span>
          <span className="address-value">{addressDetails.country}</span>
        </div>
        <div className="address-detail-row">
          <span className="address-label">Postal Code:</span>
          <span className="address-value">{addressDetails.postal_code}</span>
        </div>
      </div>

      {/* Order Input Section */}
      {orderPlacing && (
        <div className="order-form">
          <h3>Enter Order Details</h3>
          <label>
            Number of Servings:
            <input
              type="number"
              value={numServings}
              onChange={(e) => setNumServings(Number(e.target.value))}
              min="1"
            />
          </label>

          <h4>Delivery Address</h4>
          <label>
            Address Line 1:
            <input
              type="text"
              value={deliveryAddress.address_1}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, address_1: e.target.value })}
            />
          </label>
          <label>
            Address Line 2:
            <input
              type="text"
              value={deliveryAddress.address_2}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, address_2: e.target.value })}
            />
          </label>
          <label>
            City:
            <input
              type="text"
              value={deliveryAddress.city}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, city: e.target.value })}
            />
          </label>
          <label>
            State:
            <input
              type="text"
              value={deliveryAddress.state}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, state: e.target.value })}
            />
          </label>
          <label>
            Country:
            <input
              type="text"
              value={deliveryAddress.country}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, country: e.target.value })}
            />
          </label>
          <label>
            Postal Code:
            <input
              type="text"
              value={deliveryAddress.postal_code}
              onChange={(e) => setDeliveryAddress({ ...deliveryAddress, postal_code: e.target.value })}
            />
          </label>
        </div>
      )}

      {/* Order Button Section */}
      
        <div className="order-button-container">
          {orderPlacing ? <button className="order-button" onClick={handleOrder}>
            Place Order
          </button> : <button className="order-button" onClick={handleGetItClick}>
            Get it
            </button>}
        </div>
    </div>
  );
}
