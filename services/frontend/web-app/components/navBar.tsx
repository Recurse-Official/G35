"use client";
import React, { useEffect } from "react";
import Link from "next/link";
import "./styles/navBar.css";

export function NavBar() {
  useEffect(() => {
    window.localStorage.setItem("user_email", "user1@example.com");
  }, []);
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link className="navbar-brand" href="/">ShareMeal</Link>
      </div>
    </nav>
  );
}
