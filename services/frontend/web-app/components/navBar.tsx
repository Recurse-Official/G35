"use client";
import Link from "next/link";
import "./styles/navBar.css";

export function NavBar() {
  const isLoggedIn = localStorage.getItem("loggedIn");
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link className="navbar-brand" href="/home">ShareMeal</Link>
      </div>
      {isLoggedIn ? <div>
        <div className="sidebar-item">
          <Link href="/immediate">Immediate Need</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/find">Find Food</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/donate">Donate Food</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/profile">Profile</Link>
        </div>
      </div> : <div></div>}
    </nav>
  );
}
