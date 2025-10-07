package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class MyMembership(
    @SerializedName("id")
    val id: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("membership_package_id")
    val membershipPackageId: Int,
    @SerializedName("deleted_at")
    val deletedAt: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("membership_package")
    val membershipPackage: MembershipPackage
)
