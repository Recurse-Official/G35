package com.example.sharemeal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class FoodOrderRequest(
    val user_id: Int?,
    val food_available_id: Long,
    val num_servings: Int,
    val address_id: Int,
    val status: String?,
    val address_1: String,
    val address_2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postal_code: String,
    val latitude: Double,
    val longitude: Double
)
interface FoodOrderRequestApi {
    @POST("/orders/create")  // Replace with your backend endpoint
     fun submitFoodOrder(@Body foodOrderRequest: FoodOrderRequest): Call<Void>
}
 fun submitFoodOrder(foodOrderRequest: FoodOrderRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val call = RetrofitClient.foodOrderRequestApi.submitFoodOrder(foodOrderRequest)
    call.enqueue(object : retrofit2.Callback<Void> {
        override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
            if (response.isSuccessful) {
                onSuccess() // Order successfully placed
            } else {
                onError("Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            onError("Failed to connect: ${t.localizedMessage}")
        }
    })
}


