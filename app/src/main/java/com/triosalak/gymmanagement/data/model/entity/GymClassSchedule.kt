package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

data class GymClassSchedule(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("start_time")
    val startTime: String? = null,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("slot")
    val slot: Int? = null,

    @SerializedName("available_slot")
    val availableSlot: Int? = null,

    @SerializedName("gym_class_id")
    val gymClassId: Int? = null
)
