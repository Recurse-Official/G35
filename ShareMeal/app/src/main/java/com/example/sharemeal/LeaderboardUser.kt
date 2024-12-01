package com.example.sharemeal

import retrofit2.http.GET

data class LeaderboardUser(
    val name: String,
    val plates: Int
)
interface LeaderboardApi {
    @GET("users/leaderboard")
    suspend fun getLeaderboard(): List<LeaderboardUser>
}
