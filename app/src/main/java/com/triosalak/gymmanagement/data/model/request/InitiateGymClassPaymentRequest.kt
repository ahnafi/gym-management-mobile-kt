package com.triosalak.gymmanagement.data.model.request

import com.google.gson.annotations.SerializedName

data class InitiateGymClassPaymentRequest(
    @SerializedName("gym_class_id")
    val gymClassId: Int,
    @SerializedName("gym_class_schedule_id")
    val gymClassScheduleId: Int,
)
