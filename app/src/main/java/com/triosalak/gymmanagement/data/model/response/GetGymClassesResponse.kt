package com.triosalak.gymmanagement.data.model.response


import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.entity.GymClass

data class GetGymClassesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("data")
    val data: GymClassesData
)

data class GymClassesData(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("data")
    val classes: List<GymClass>,

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

data class PaginationLink(
    @SerializedName("url")
    val url: String?,

    @SerializedName("label")
    val label: String,

    @SerializedName("page")
    val page: Int?,

    @SerializedName("active")
    val active: Boolean
)
