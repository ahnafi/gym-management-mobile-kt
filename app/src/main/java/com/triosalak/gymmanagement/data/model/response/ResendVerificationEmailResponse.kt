package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName

data class ResendVerificationEmailResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
