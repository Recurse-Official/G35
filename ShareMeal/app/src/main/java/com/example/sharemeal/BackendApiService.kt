package com.example.sharemeal

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendApiService {
    @GET("/users/id/")
    fun getUserByEmail(@Query("email") email: String): Call<UserResponse>
}

data class UserResponse(
    val id: Int,
    val email: String,
    val username: String?,
    val full_name: String?,
    val phone: String?,
    val user_type: String?,
    val status: String?
)
fun fetchUserDetails(email: String, onSuccess: (UserResponse) -> Unit, onError: (String) -> Unit) {
    val call = RetrofitClient.backendApiService.getUserByEmail(email)
    call.enqueue(object : retrofit2.Callback<UserResponse> {
        override fun onResponse(call: Call<UserResponse>, response: retrofit2.Response<UserResponse>) {
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    onSuccess(user)
                } else {
                    onError("No user found for this email.")
                }
            } else {
                onError("Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            onError("Failed to connect: ${t.localizedMessage}")
        }
    })
}