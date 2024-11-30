"use client";
import { useState, useEffect } from "react";
import axios from "axios";
import { BACKEND_URL } from "@/constants";
import "./profile_style.css"

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

interface OrderModel {
  order: {
    id: number;
    user_id: number;
    food_available_id: number;
    address_id: number;
    delivery_address_id: number;
    num_servings: number;
    created_at: string;
    status: string;
  };
  food: {
    id: number;
    user_id: number;
    food_type: number;
    food_title: string;
    food_available: string;
    num_servings: number;
    num_servings_left: number;
    prepared_date: string;
    expiration_date: string;
    created_at: string;
    address_id: number;
    status: string;
  };
}

interface DonationModel {
  id: number;
  user_id: number;
  food_type: number;
  food_title: string;
  food_available: string;
  num_servings: number;
  num_servings_left: number;
  prepared_date: string;
  expiration_date: string;
  created_at: string;
  address_id: number;
  status: string;
}

async function getUserByEmail(email: string) {
  const response = await axios.get(`${BACKEND_URL}/users/id/`, {
    params: { email: email },
  });
  return response.data;
}

async function getUserDonations(userId: number) {
  const response = await axios.get(`${BACKEND_URL}/users/get/donations/`, {
    params: { user_id: userId },
  });
  return response.data;
}

async function getUserOrders(userId: number) {
  const response = await axios.get(`${BACKEND_URL}/users/get/orders/`, {
    params: { user_id: userId },
  });
  return response.data;
}

export default function ProfilePage() {
  const [user, setUser] = useState({} as UserDetails);
  const email = localStorage.getItem("user_email");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [donations, setDonations] = useState<DonationModel[]>([]);
  const [orders, setOrders] = useState<OrderModel[]>([]);
  const [activeTab, setActiveTab] = useState<"orders" | "donations">("orders");

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
          setError(true);
          setLoading(false);
        });
    }
  }, [email]);

  useEffect(() => {
    const user_id = user.id;
    if (user_id) {
      setLoading(true);
      Promise.all([getUserDonations(user_id), getUserOrders(user_id)])
        .then(([donations, orders]) => {
          setDonations(donations);
          setOrders(orders);
          setLoading(false);
        })
        .catch(() => {
          setError(true);
          setLoading(false);
        });
    }
  }, [user]);

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
      <h1>{user.full_name}</h1>
      <div className="profile-info">
        <div className="profile-detail">
          <strong>Email:</strong> {user.email}
        </div>
        <div className="profile-detail">
          <strong>Username:</strong> {user.username || "N/A"}
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
      </div>
      <div className="data-section">
        <div className="tabs">
          <div
            className={`tab ${activeTab === "orders" ? "active" : ""}`}
            onClick={() => setActiveTab("orders")}
          >
            Orders
          </div>
          <div
            className={`tab ${activeTab === "donations" ? "active" : ""}`}
            onClick={() => setActiveTab("donations")}
          >
            Donations
          </div>
        </div>
        <div className="data-display">
          {activeTab === "orders" && (
            <div className="orders">
              {orders.map((order) => (
                <div key={order.order.id} className="order-card">
                  <strong>Order ID:</strong> {order.order.id}
                  <div>
                    <strong>Status:</strong> {order.order.status}
                  </div>
                  <div>
                    <strong>Food Title:</strong> {order.food.food_title}
                  </div>
                  <div>
                    <strong>Servings:</strong> {order.order.num_servings}
                  </div>
                  <div>
                    <strong>Created At:</strong>{" "}
                    {new Date(order.order.created_at).toLocaleDateString()}
                  </div>
                </div>
              ))}
            </div>
          )}
          {activeTab === "donations" && (
            <div className="donations">
              {donations.map((donation) => (
                <div key={donation.id} className="donation-card">
                  <strong>Donation ID:</strong> {donation.id}
                  <div>
                    <strong>Food Title:</strong> {donation.food_title}
                  </div>
                  <div>
                    <strong>Servings Left:</strong> {donation.num_servings_left}
                  </div>
                  <div>
                    <strong>Expiration Date:</strong>{" "}
                    {new Date(donation.expiration_date).toLocaleDateString()}
                  </div>
                  <div>
                    <strong>Status:</strong> {donation.status}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
