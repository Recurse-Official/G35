"use client";
import { useEffect, useState } from "react";
import "./styles.css";
import { BACKEND_URL } from "@/constants";
import axios from "axios";

export default function ImmediatePage() {
  const user_id = localStorage.getItem("user_id");
  const [formData, setFormData] = useState({
    address_1: "",
    address_2: "",
    city: "",
    state: "",
    country: "",
    postal_code: "",
    num_servings: "", // New field for number of servings
  });
  const [latitude, setLatitude] = useState(0);
  const [longitude, setLongitude] = useState(0);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition((position) => {
      setLatitude(position.coords.latitude);
      setLongitude(position.coords.longitude);
    });
  }
  , []);
  useEffect(() => {
    if (typeof window !== "undefined") {
        const isLoggedIn = localStorage.getItem("loggedIn");

        if (!isLoggedIn) {
            const currentPath = window.location.pathname;
            if (currentPath !== "/login" && currentPath !== "/signup") {
                window.location.href = "/login";
            }
        }
    }
}, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    axios.post(`${BACKEND_URL}/food/immediate`, {
      ...formData,
      user_id,
      latitude,
      longitude,
    }).then((response) => {
      console.log(response.data);
      if (response.data.status === "ok") {
        alert("Nearest food ordered successfully")
        window.location.href = "/home";
      } else {
        alert("Failed to order food");
      }
    }
    ).catch((error) => {
      console.error(error);
    }
    )
  };

  return (
    <div>
      <h2 className="title">Find readily available food nearby</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="address_1">Address Line 1</label>
          <input
            type="text"
            id="address_1"
            name="address_1"
            value={formData.address_1}
            onChange={handleChange}
            placeholder="Enter Address Line 1"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="address_2">Address Line 2</label>
          <input
            type="text"
            id="address_2"
            name="address_2"
            value={formData.address_2}
            onChange={handleChange}
            placeholder="Enter Address Line 2"
          />
        </div>

        <div className="form-group">
          <label htmlFor="city">City</label>
          <input
            type="text"
            id="city"
            name="city"
            value={formData.city}
            onChange={handleChange}
            placeholder="Enter City"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="state">State</label>
          <input
            type="text"
            id="state"
            name="state"
            value={formData.state}
            onChange={handleChange}
            placeholder="Enter State"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="country">Country</label>
          <input
            type="text"
            id="country"
            name="country"
            value={formData.country}
            onChange={handleChange}
            placeholder="Enter Country"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="postal_code">Postal Code</label>
          <input
            type="text"
            id="postal_code"
            name="postal_code"
            value={formData.postal_code}
            onChange={handleChange}
            placeholder="Enter Postal Code"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="servings_required">Number of Servings</label>
          <input
            type="number"
            id="num_servings"
            name="num_servings"
            value={formData.num_servings}
            onChange={handleChange}
            placeholder="Enter Number of Servings"
            required
          />
        </div>

        <button type="submit">Find Immediate Food</button>
      </form>
    </div>
  );
}
