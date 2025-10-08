package com.triosalak.gymmanagement.data.model.response

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.data.model.entity.GymClassSchedule

data class GetGymClassSchedulesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: GymClassScheduleData
)

data class GymClassScheduleData(
    @SerializedName("class")
    val gymClass: GymClass,
    @SerializedName("schedules")
    val schedules: GymClassSchedulePagination
)

data class GymClassSchedulePagination(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("data")
    val data: List<GymClassSchedule>,

    @SerializedName("first_page_url")
    val firstPageUrl: String,

    @SerializedName("from")
    val from: Int,

    @SerializedName("last_page")
    val lastPage: Int,

    @SerializedName("last_page_url")
    val lastPageUrl: String,

    @SerializedName("links")
    val links: List<PaginationLink>,

    @SerializedName("next_page_url")
    val nextPageUrl: String?,

    @SerializedName("path")
    val path: String,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("prev_page_url")
    val prevPageUrl: String?,

    @SerializedName("to")
    val to: Int,

    @SerializedName("total")
    val total: Int
)
