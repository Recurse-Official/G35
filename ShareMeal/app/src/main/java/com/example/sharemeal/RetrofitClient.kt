package com.example.sharemeal

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.182.212" // Replace with your backend's IP and port

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val backendApiService: BackendApiService by lazy {
        retrofit.create(BackendApiService::class.java)
    }

    val foodDonationApi: FoodDonationApi by lazy {
        retrofit.create(FoodDonationApi::class.java)
    }

    val availableFoodApi: AvailableFoodApi by lazy {
        retrofit.create(AvailableFoodApi::class.java)
    }

    val leaderboardApi: LeaderboardApi by lazy {
        retrofit.create(LeaderboardApi::class.java)
    }
}
