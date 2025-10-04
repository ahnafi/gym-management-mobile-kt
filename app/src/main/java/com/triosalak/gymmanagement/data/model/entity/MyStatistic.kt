package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class MyStatistic(
    @SerializedName("total_visits")
    val totalVisits: Int,
    @SerializedName("visits_this_month")
    val visitsThisMonth: Int,
    @SerializedName("visits_last_month")
    val visitsLastMonth: Int,
    @SerializedName("average_visits_per_week")
    val averageVisitsPerWeek: Double,
    @SerializedName("longest_streak")
    val longestStreak: Int,
    @SerializedName("current_streak")
    val currentStreak: Int,
    @SerializedName("favorite_visit_time")
    val favoriteVisitTime: String?,
    @SerializedName("recent_visits")
    val recentVisits: List<RecentVisit>
)

data class RecentVisit(
    @SerializedName("id")
    val id: Int,
    @SerializedName("visit_date")
    val visitDate: String,
    @SerializedName("entry_time")
    val entryTime: String,
    @SerializedName("exit_time")
    val exitTime: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
