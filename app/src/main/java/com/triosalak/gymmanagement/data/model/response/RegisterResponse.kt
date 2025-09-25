package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LoginData
)