package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class GymClass(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("images")
    val images: List<String>? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("gym_class_schedules")
    val gymClassSchedules: List<GymClassSchedule>? = null,
)
