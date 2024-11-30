"use client";
import React, { useEffect, useState } from "react";
import axios from "axios";
import "./styles.css";
import { BACKEND_URL } from "@/constants";

export default function DonatePage() {
    const user_id = localStorage.getItem("user_id");
    const [latitude, setLatitude] = useState(0.0);
    const [longitude, setLongitude] = useState(0.0);

    const [formData, setFormData] = useState({
        food_type: "Veg",
        food_title: "",
        food_available: "",
        num_servings: 0,
        prepared_date: "",
        expiration_date: "",
        address_1: "",
        address_2: "",
        city: "",
        state: "",
        country: "",
        postal_code: "",
    });

    const [statusMessage, setStatusMessage] = useState("");

    useEffect(() => {
        navigator.geolocation.getCurrentPosition((position) => {
            setLatitude(position.coords.latitude);
            setLongitude(position.coords.longitude);
        });
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleDonate = async () => {
        try {
            const response = await axios.post(`${BACKEND_URL}/food/add`, {
                ...formData,
                user_id,
                latitude,
                longitude,
            });
            if (response.status == 200) {
                alert("Food donated successfully");
                window.location.href = "/home";
            } else {
                alert("Error donating food, please try again");
            }
        } catch (error) {
            alert("Error donating food, please try again");
        }
    };

    return (
        <div className="donate-page-container">
            <h1 className="donate-page-title">Donate Food</h1>
            <p className="donate-page-description">Please fill out the form below to donate food.</p>

            <div className="form-group">
                <label>Food Type:</label>
                <select name="food_type" value={formData.food_type} onChange={handleChange}>
                    <option value="Veg">Veg</option>
                    <option value="Non-Veg">Non-Veg</option>
                </select>
            </div>

            <div className="form-group">
                <label>Food Title:</label>
                <input
                    type="text"
                    name="food_title"
                    value={formData.food_title}
                    onChange={handleChange}
                    placeholder="Enter food title"
                    required
                />
            </div>

            <div className="form-group">
                <label>Food Available:</label>
                <input
                    type="text"
                    name="food_available"
                    value={formData.food_available}
                    onChange={handleChange}
                    placeholder="Describe available food"
                />
            </div>

            <div className="form-group">
                <label>Number of Servings:</label>
                <input
                    type="number"
                    name="num_servings"
                    value={formData.num_servings}
                    onChange={handleChange}
                    placeholder="Enter number of servings"
                    required
                />
            </div>

            <div className="form-group">
                <label>Prepared Date:</label>
                <input
                    type="date"
                    name="prepared_date"
                    value={formData.prepared_date}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Expiration Date:</label>
                <input
                    type="date"
                    name="expiration_date"
                    value={formData.expiration_date}
                    onChange={handleChange}
                    required
                />
            </div>

            <h2 className="form-section-title">Address</h2>
            <div className="form-group">
                <label>Address Line 1:</label>
                <input
                    type="text"
                    name="address_1"
                    value={formData.address_1}
                    onChange={handleChange}
                    placeholder="Address line 1"
                    required
                />
            </div>

            <div className="form-group">
                <label>Address Line 2:</label>
                <input
                    type="text"
                    name="address_2"
                    value={formData.address_2}
                    onChange={handleChange}
                    placeholder="Address line 2 (optional)"
                />
            </div>

            <div className="form-group">
                <label>City:</label>
                <input
                    type="text"
                    name="city"
                    value={formData.city}
                    onChange={handleChange}
                    placeholder="City"
                    required
                />
            </div>

            <div className="form-group">
                <label>State:</label>
                <input
                    type="text"
                    name="state"
                    value={formData.state}
                    onChange={handleChange}
                    placeholder="State"
                    required
                />
            </div>

            <div className="form-group">
                <label>Country:</label>
                <input
                    type="text"
                    name="country"
                    value={formData.country}
                    onChange={handleChange}
                    placeholder="Country"
                    required
                />
            </div>

            <div className="form-group">
                <label>Postal Code:</label>
                <input
                    type="text"
                    name="postal_code"
                    value={formData.postal_code}
                    onChange={handleChange}
                    placeholder="Postal code"
                    required
                />
            </div>

            <p className="donate-page-description">Please make sure the food has not gone bad before you donate</p>
            <p className="donate-page-description">Thank you</p>
            <button className="donate-button" onClick={handleDonate}>
                Donate
            </button>

            {statusMessage && (
                <p
                    className={`status-message ${
                        statusMessage.includes("Error") ? "error" : "success"
                    }`}
                >
                    {statusMessage}
                </p>
            )}
        </div>
    );
}
