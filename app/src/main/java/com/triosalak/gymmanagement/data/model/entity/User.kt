package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("membership_registered")
    val membershipRegistered: String? = null,
    @SerializedName("membership_status")
    val membershipStatus: String? = null,
    @SerializedName("membership_end_date")
    val membershipEndDate: String? = null,
    @SerializedName("profile_bio")
    val profileBio: String? = null,
    @SerializedName("profile_image")
    val profileImage: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null
)