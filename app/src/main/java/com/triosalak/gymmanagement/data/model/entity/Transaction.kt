package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("gym_class_schedule_id")
    val gymClassScheduleId: Int? = null,
    @SerializedName("amount")
    val amount: Int? = null,
    @SerializedName("snap_token")
    val snapToken: String? = null,
    @SerializedName("payment_date")
    val paymentDate: String? = null,
    @SerializedName("payment_status")
    val paymentStatus: String? = null,
    @SerializedName("purchasable_type")
    val purchasableType: String? = null,
    @SerializedName("purchasable_id")
    val purchasableId: Int? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("purchasable")
    val purchasable: Purchasable? = null
)
