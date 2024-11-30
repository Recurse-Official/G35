"use client";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "@/constants";

interface UserDetails {
  id: number;
  email: string;
  username: string;
  full_name: string;
  phone: string;
  status: string;
  created_at: string;
  updated_at: string;
}

async function getUserByEmail(email: string) {
  const response = await axios.get(`${BACKEND_URL}/users/id/`, {
    params: { email: email },
  });
  return response.data;
}

async function getUserDonations(userId: number) {
  const response = await axios.get(`${BACKEND_URL}/users/get/donations/`, {
    params: { userId: userId },
  });
  return response.data;
}

async function getUserOrders(userId: number) {
  const response = await axios.get(`${BACKEND_URL}/users/get/orders/`, {
    params: { userId: userId },
  });
  return response.data;
}

export default function ProfilePage() {
  const [user, setUser] = useState({} as UserDetails);
  const email = localStorage.getItem("user_email");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false); // Set `error` to accept any type.
  const [donations, setDonations] = useState([]);
  const [orders, setOrders] = useState([]);

  // Fetch user data based on the email
  useEffect(() => {
    if (email) {
      setLoading(true);
      getUserByEmail(email)
        .then((user) => {
          setUser(user);
          setLoading(false);
        })
        .catch(() => {
          setError(true); // Store error object
          setLoading(false);
        });
    }
  }, [email]);

  useEffect(() => {
    const user_id = user.id;
    if (user_id) {
      setLoading(true);
      getUserDonations(user_id)
        .then((donations) => {
          setDonations(donations);
          getUserOrders(user_id)
          .then((orders) => {
            setOrders(orders);
            setLoading(false);
          })
          setLoading(false);
        })
        .catch(() => {
          setError(true);
          setLoading(false);
        });
      }
  }
  , [user]);

  // Render loading state, error, or user profile
  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error loading user profile</div>;
  }

  if (!user) {
    return <div>No user found</div>;
  }

  return (
    <div className="profile-page">
      <h1>User Profile</h1>
      <div className="profile-info">
        <div className="profile-detail">
          <strong>Email:</strong> {user.email}
        </div>
        <div className="profile-detail">
          <strong>Username:</strong> {user.username || "N/A"}
        </div>
        <div className="profile-detail">
          <strong>Full Name:</strong> {user.full_name || "N/A"}
        </div>
        <div className="profile-detail">
          <strong>Phone:</strong> {user.phone || "N/A"}
        </div>
        <div className="profile-detail">
          <strong>Status:</strong> {user.status || "Active"}
        </div>
        <div className="profile-detail">
          <strong>Account Created At:</strong>{" "}
          {new Date(user.created_at).toLocaleDateString()}
        </div>
        <div className="profile-detail">
          <strong>Last Updated At:</strong>{" "}
          {new Date(user.updated_at).toLocaleDateString()}
        </div>
      </div>
    </div>
  );
}
