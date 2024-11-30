import React from "react";
import Link from "next/link";
import "./styles/navBar.css";

export function NavBar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link className="navbar-brand" href="/">ShareMeal</Link>
      </div>
    </nav>
  );
}
