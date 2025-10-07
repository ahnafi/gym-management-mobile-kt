package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.Transaction

data class GetTransactionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: Transaction
)
