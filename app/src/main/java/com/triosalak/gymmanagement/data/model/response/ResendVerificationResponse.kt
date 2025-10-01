package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName

data class ResendVerificationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
)
