package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.User

data class CurrentUserResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: User
)
