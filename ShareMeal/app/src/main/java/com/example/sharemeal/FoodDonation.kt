package com.example.sharemeal
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
data class FoodDonation(
    val user_id:Int,
    val food_type: String,
    val food_title: String,
    val food_available: String,
    val num_servings: Int,
    val prepared_date: String,
    val expiration_date: String,
    val address_1: String,
    val address_2: String,
    val city: String,
    val state: String,
    val country: String,
    val postal_code: String,
    val latitude: Double,
    val longitude: Double
)
fun addDonation(donation: FoodDonation, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val call = RetrofitClient.foodDonationApi.addDonation(donation)
    call.enqueue(object : retrofit2.Callback<Void> {
        override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
            if (response.isSuccessful) {
                onSuccess() // Donation added successfully
            } else {
                onError("Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            onError("Failed to connect: ${t.localizedMessage}")
        }
    })
}
interface FoodDonationApi {

    @POST("/food/add")
    fun addDonation(@Body donation: FoodDonation): Call<Void> // Assuming the server doesn't return any data after adding the donation
}