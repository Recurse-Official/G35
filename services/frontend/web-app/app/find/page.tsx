"use client";

import { useState, useEffect } from "react";
import { BACKEND_URL, RADIUS_KM } from "@/constants";
import { FoodTileProps, FoodTileContainer } from "@/components/foodTile";

export default function FindPage() {
    // const [location, setLocation] = useState({ latitude: 0, longitude: 0 });
    const [results, setResults] = useState([] as FoodTileProps[]);

    useEffect(() => {
        // Fetch user location when the component mounts
        navigator.geolocation.getCurrentPosition(handleSuccessLocation, handleErrorLocation);

        function handleSuccessLocation(position: GeolocationPosition) {
            const { latitude, longitude } = position.coords;

            // Fetch nearby available food once location is set
            fetch(`${BACKEND_URL}/food/nearby?lat=${latitude}&long=${longitude}&radius_km=${RADIUS_KM}`)
                .then((response) => response.json())
                .then((data) => setResults(data as FoodTileProps[]))
                .catch((error) => console.error("Error fetching food data:", error));
        }

        function handleErrorLocation(error: GeolocationPositionError) {
            console.error("Error getting location:", error);
        }
    }, []);

    return (
        <div>
            <FoodTileContainer nearbyFood={results} />
        </div>
    );
}
