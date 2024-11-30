"use client";
import React, { useState, useEffect } from "react";
import { Bar, Pie, Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  ArcElement,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
} from "chart.js";
import './styles.css'; // Regular import of global CSS

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement
);

const AnalyticsPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);

  const barData = {
    labels: [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec",
    ],
    datasets: [
      {
        label: "Donations (kg)",
        data: [200, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400],
        backgroundColor: "#42a5f5",
      },
    ],
  };

  const pieData = {
    labels: ["Individuals", "Corporates", "Nonprofits"],
    datasets: [
      {
        data: [50, 30, 20],
        backgroundColor: ["#ff6384", "#ffcd56", "#36a2eb"],
      },
    ],
  };

  const lineData = {
    labels: [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec",
    ],
    datasets: [
      {
        label: "Food Saved (kg)",
        data: [100, 200, 300, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000],
        borderColor: "#8e44ad",
        backgroundColor: "rgba(142, 68, 173, 0.2)",
        borderWidth: 2,
      },
    ],
  };

  useEffect(() => {
    // const timer = setTimeout(() => setLoading(false), 1000);
    // return () => clearTimeout(timer); // Cleanup timer on unmount
  }, []);

  return (
    <div className="analyticsPage">
      <h1 className="pageTitle">Analytics Dashboard</h1>

      {loading ? (
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading Data...</p>
        </div>
      ) : (
        <>
          <div className="cardsContainer">
            <div className="card">
              <h2>Monthly Donations</h2>
              <div className="chartWrapper">
                <Bar
                  data={barData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                  }}
                />
              </div>
            </div>
            <div className="card">
              <h2>Donor Categories</h2>
              <div className="chartWrapper">
                <Pie
                  data={pieData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                  }}
                />
              </div>
            </div>
            <div className="card">
              <h2>Food Saved Over Time</h2>
              <div className="chartWrapper">
                <Line
                  data={lineData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                  }}
                />
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default AnalyticsPage;
