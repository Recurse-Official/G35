"use client";
import "./styles/foodTile.css";
import { useState, useEffect } from "react";

export interface FoodTileProps {
  id: BigInteger;
  user_id: BigInteger;
  food_title: string;
  food_available: string;
  num_servings: number;
  prepared_date: string;
  expiration_date: string;
  status: string;
  distance: number;
}

// eslint-disable-next-line @next/next/no-img-element
export function FoodTileContainer({ nearbyFood }: { nearbyFood: FoodTileProps[] }) {
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Simulate a loading delay to show the loading state
    const timeout = setTimeout(() => {
      setIsLoading(false); // Data loading is complete
    }, 1000); // Adjust the duration as needed

    return () => clearTimeout(timeout); // Cleanup the timeout
  }, [nearbyFood]);

  function handleFoodTileClick(id: BigInteger) {
    window.location.href = `/food/${id}`;
  }

  return (
    <div className="food-tile-container">
      <div className="find-food-text-wrapper">
      </div>
      {isLoading ? (
        <div className="loading-indicator">Finding Food Near You :{")"}</div>
      ) : (
        <div>
          <div className="find-food-text">Food near you:</div>
          <div className="food-tile-grid">
            {nearbyFood.map((food) => (
              <div
                className="food-tile"
                key={food.id.toString()}
                onClick={() => handleFoodTileClick(food.id)}
              >
                {/* Left Section */}
                <div className="food-tile-left">
                  <h3>{food.food_title}</h3>
                  <p>{food.food_available}</p>
                  <div className="food-info-line">
                    <span>Servings: {food.num_servings}</span>
                    <span>Distance: {food.distance.toFixed(1)} km</span>
                    <span>Prepared: {new Date(food.prepared_date).toLocaleDateString()}</span>
                    <span>Expiration: {new Date(food.expiration_date).toLocaleDateString()}</span>
                  </div>
                </div>
                {/* Right Section */}
                <div className="food-tile-right">
                  <img
                    className="food-tile-image"
                    src={`https://i.ytimg.com/vi/oskLFFcvhGI/maxresdefault.jpg`} /* Replace with actual image URL */
                    alt={food.food_title}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
