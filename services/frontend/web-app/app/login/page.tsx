"use client";
import React, { useState } from "react";
import axios from "axios";
import { BACKEND_URL } from "@/constants";
import "./styles.css";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [statusMessage, setStatusMessage] = useState("");

    const handleLogin = async () => {
        try {
            const response = await axios.get(`${BACKEND_URL}/users/login`, {
                params: {
                    email,
                    password,
                },
            });

            if (response.status === 200) {
                localStorage.setItem("user_id", response.data.user.id);
                localStorage.setItem("loggedIn", "true");

                await axios
                    .get(`${BACKEND_URL}/users/id/`, { params: { email: response.data.user.email } })
                    .then((res) => {
                        window.localStorage.setItem("user_id", res.data.id);
                        window.localStorage.setItem("username", res.data.username);
                        window.localStorage.setItem("phone", res.data.phone);
                        window.localStorage.setItem("full_name", res.data.full_name);
                        window.localStorage.setItem("status", res.data.status);
                    });

                window.location.href = "/home";
            } else {
                setStatusMessage("Invalid email or password");
            }
        } catch (error) {
            setStatusMessage(`Invalid email or password: ${error}`);
        }
    };

    return (
        <div className="login-container">
            <h1 className="title">Login</h1>
            <div className="form-container">
                <div className="input-container">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="text-input"
                    />
                </div>
                <div className="input-container">
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="text-input"
                    />
                </div>
                <button className="login-button" onClick={handleLogin}>
                    Login
                </button>
                {statusMessage && <p className="status-message">{statusMessage}</p>}
            </div>
        </div>
    );
}
