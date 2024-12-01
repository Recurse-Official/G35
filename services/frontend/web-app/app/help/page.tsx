"use client";
import React, { useEffect } from "react";


export default function HelpPage() {
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
    return (
      <div>
        Help Page
      </div>
    );
  }
