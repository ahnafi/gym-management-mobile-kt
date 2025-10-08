package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.data.model.entity.MembershipPackage
import com.triosalak.gymmanagement.data.model.entity.Transaction

data class InitiateGymClassPaymentResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: InitiateGymClassPaymentData?
)

data class InitiateGymClassPaymentData(
    @SerializedName("transaction")
    val transaction: Transaction,
    @SerializedName("snap_token")
    val snapToken: String,
    @SerializedName("gym_class")
    val gym_class: GymClass
)