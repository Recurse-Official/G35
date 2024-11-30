package com.example.sharemeal
import retrofit2.http.GET

data class AvailableFood(
    val id: Long,
    val user_id: Long,
    val food_title: String,
    val food_available: String,
    val num_servings: Long,
    val prepared_date: String?,
    val expiration_date: String?,
    val status: String?,
    val distance: Double // Added 'distance' field
)

interface AvailableFoodApi {
    @GET("/food/nearby") // Adjust the endpoint as per your backend's API
    suspend fun getNearbyFood(): List<AvailableFood>
}

