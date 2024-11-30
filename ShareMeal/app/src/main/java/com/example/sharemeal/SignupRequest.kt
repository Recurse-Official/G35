package com.example.sharemeal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    val email: String,
    val username: String?,
    val full_name: String?,
    val phone: String?,
    val password: String,
    val status: String? = "active" // Optional default value
)

data class SignupResponse(
    val user: User,
    val status: String
)

data class User(
    val id: Int,
    val email: String,
    val username: String?,
    val full_name: String?,
    val phone: String?,
    val user_type: String?,
    val status: String?
)

interface ApiService {
    @POST("/users/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
}
