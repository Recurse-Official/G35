"use client";
import { useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "@/constants";
import "./styles.css"; // Assume you have a CSS file for styling

export default function SignUpPage() {
    const [formData, setFormData] = useState({
        email: "",
        password: "",
        confirmPassword: "",
    });

    const [statusMessage, setStatusMessage] = useState("");
    const [statusType, setStatusType] = useState<"success" | "error" | "">("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setStatusMessage("");
        setStatusType("");

        const { email, password, confirmPassword } = formData;

        if (password !== confirmPassword) {
            setStatusMessage("Passwords do not match!");
            setStatusType("error");
            return;
        }

        try {
            const response = await axios.post(`${BACKEND_URL}/signup`, {
                email,
                password,
            });

            if (response.data.status === "ok") {
                setStatusMessage("Account created successfully! Please log in.");
                setStatusType("success");
            } else {
                setStatusMessage("Failed to create an account. Try again.");
                setStatusType("error");
            }
        } catch (error) {
            setStatusMessage(error as string || "An error occurred.");
            setStatusType("error");
        }
    };

    return (
        <div className="signup-container">
            <h1 className="title">Sign Up</h1>
            <form className="form-container" onSubmit={handleSubmit}>
                <div className="input-container">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="input-container">
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="input-container">
                    <label>Confirm Password</label>
                    <input
                        type="password"
                        name="confirmPassword"
                        value={formData.confirmPassword}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit">Sign Up</button>
            </form>

            {statusMessage && (
                <div className={`status-message ${statusType}`}>
                    {statusMessage}
                </div>
            )}
        </div>
    );
}
