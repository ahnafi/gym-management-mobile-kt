package com.triosalak.gymmanagement.data.model.request

import com.google.gson.annotations.SerializedName

data class InitiateMembershipPaymentRequest(
    @SerializedName("membership_package_id")
    val membershipPackageId: Int,
)
