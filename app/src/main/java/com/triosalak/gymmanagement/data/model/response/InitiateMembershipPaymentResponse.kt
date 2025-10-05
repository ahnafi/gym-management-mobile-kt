package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.Transaction
import com.triosalak.gymmanagement.data.model.entity.MembershipPackage

data class InitiateMembershipPaymentResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: InitiateMembershipPaymentData?
)

data class InitiateMembershipPaymentData(
    @SerializedName("transaction")
    val transaction: Transaction,
    @SerializedName("snap_token")
    val snapToken: String,
    @SerializedName("package")
    val membershipPackage: MembershipPackage
)
