import "./styles/foodTile.css";

export interface FoodTileProps {
    id: BigInteger;
    user_id: BigInteger;
    food_title: string;
    food_available: string;
    num_servings: number;
    created_at: string;
    status: string;
    distance: number;
}

// eslint-disable-next-line @next/next/no-img-element
export function FoodTileContainer({ nearbyFood }: { nearbyFood: FoodTileProps[] }) {

function handleFoodTileClick(id: BigInteger) {
    window.location.href = `/food/${id}`;
}

  return (
    <div className="food-tile-container">
      {nearbyFood.sort((a, b) => a.distance - b.distance)
      .map((food) => (
        <div className="food-tile" key={food.id.toString()} onClick={()=>handleFoodTileClick(food.id)}>
          {/* Left Section */}
          <div className="food-tile-left">
            <h3>{food.food_title}</h3>
            <p>{food.food_available}</p>
            <div className="food-info-line">
              <span>Servings: {food.num_servings}</span>
              <span>Distance: {food.distance.toFixed(1)} km</span>
              <span>Created: {new Date(food.created_at).toLocaleDateString()}</span>
              {/* <span>Status: {food.status}</span> */}
            </div>
          </div>

          {/* Right Section */}
          <img
            className="food-tile-image"
            src={`https://i.ytimg.com/vi/oskLFFcvhGI/maxresdefault.jpg`} /* Replace with actual image URL */
            alt={food.food_title}
          />
        </div>
      ))}
    </div>
  );
}

