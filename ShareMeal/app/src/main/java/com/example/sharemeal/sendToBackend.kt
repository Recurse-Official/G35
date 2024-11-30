package com.example.sharemeal

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun sendToBackend(
    fullName: String?,
    phoneNumber: String?,
    email: String,
    password: String,
    callback: (Boolean, String?) -> Unit
) {
    val request = SignupRequest(
        email = email,
        username = fullName, // Optionally provide a username
        full_name = fullName,
        phone = phoneNumber,
        password = password,
        status = "active"
    )

    val call = RetrofitClient.apiService.signup(request)
    call.enqueue(object : Callback<SignupResponse> {
        override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
            if (response.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, response.errorBody()?.string())
            }
        }

        override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
            callback(false, t.message)
        }
    })
}
