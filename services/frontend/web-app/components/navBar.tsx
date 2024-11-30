"use client";
import React, { useEffect } from "react";
import Link from "next/link";
import "./styles/navBar.css";
import axios from "axios";
import { BACKEND_URL } from "@/constants";

export function NavBar() {
  useEffect(() => {
    window.localStorage.setItem("user_email", "user1@example.com");
    axios.get(`${BACKEND_URL}/users/id/`, {
      params: { email: "user1@example.com" },
      
    }).then((response) => {
      window.localStorage.setItem("user_id", response.data.id);
      window.localStorage.setItem("username", response.data.username);
      window.localStorage.setItem("phone", response.data.phone);
      window.localStorage.setItem("full_name", response.data.full_name);
      window.localStorage.setItem("status", response.data.status);
    }
    );
  }, []);
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link className="navbar-brand" href="/">ShareMeal</Link>
      </div>
    </nav>
  );
}
