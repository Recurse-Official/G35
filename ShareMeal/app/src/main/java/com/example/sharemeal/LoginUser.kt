package com.example.sharemeal

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


data class LoginUser(
    val email: String,
    val password: String
)

interface LoginUserApi {
    @GET("/users/login/")
    fun loginUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<UserResponse>
}
fun loginUser(
    email: String,
    password: String,
    onSuccess: (UserResponse) -> Unit,
    onError: (String) -> Unit
) {
    val loginUserApi = RetrofitClient.loginUserApi

    // Make API call
    val call = loginUserApi.loginUser(email, password)
    call.enqueue(object : Callback<UserResponse> {
        override fun onResponse(
            call: Call<UserResponse>,
            response: Response<UserResponse>
        ) {
            if (response.isSuccessful && response.body() != null) {
                val userResponse = response.body()!!

                // Capture the userId from the response
                val userId = userResponse.id
                val name = userResponse.full_name

                // Call the onSuccess callback with the userResponse
                onSuccess(userResponse)

                // Optionally, you can print the userId or show it in a Toast
                println("User ID: $userId")
                println("User Name: $name")
            } else {
                // API returned an error
                val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                onError("Login failed: $errorMessage")
            }
        }

        override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            // Network or other errors
            onError("Network error: ${t.message}")
        }
    })
}
