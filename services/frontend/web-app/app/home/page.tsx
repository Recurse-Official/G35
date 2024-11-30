"use client";
import { useState, useEffect } from "react";
import { BACKEND_URL } from "@/constants";
import FindFood from "@/components/findFood";
import "./styles.css";

interface User {
    name: string;
    plates: number;
}

export default function HomePage() {
    const [leaderboard, setLeaderboard] = useState([] as User[]);

    useEffect(() => {
        async function fetchLeaderboard() {
            try {
                const response = await fetch(`${BACKEND_URL}/users/leaderboard`); // Adjust API endpoint if needed
                const data = await response.json();
                setLeaderboard(data);
            } catch (error) {
                console.error("Error fetching leaderboard:", error);
            }
        }

        fetchLeaderboard();
    }, []);

    return (
        <div>
            <div className="container">
                <h1 className="title">Weekly Leaderboard</h1>
                <table className="table">
                    <thead>
                        <tr>
                            <th className="th">Name</th>
                            <th className="th">Plates</th>
                        </tr>
                    </thead>
                    <tbody>
                        {leaderboard.map((user, index) => (
                            <tr key={index} className="row">
                                <td className="td">{user.name || "Anonymous"}</td>
                                <td className="td">{user.plates}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <div className="find-food-container">
                <div className="find-food-component">
                    <FindFood />
                </div>
            </div>
        </div>
    );
}
