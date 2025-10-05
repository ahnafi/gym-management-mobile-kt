package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.utils.Constants

data class MembershipPackage(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName ("slug")
    val slug: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("duration")
    val duration: Int? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("images")
    val images: List<String>? = null, // Back to List<String> as API returns array
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    // Computed property to get first image URL with full path
    val firstImageUrl: String?
        get() = images?.firstOrNull()?.let { imagePath ->
            if (imagePath.startsWith("http")) {
                imagePath
            } else {
                "${Constants.STORAGE_URL}$imagePath"
            }
        }
    
    // Computed property to get all image URLs with full paths
    val imageUrls: List<String>
        get() = images?.map { imagePath ->
            if (imagePath.startsWith("http")) {
                imagePath
            } else {
                "${Constants.STORAGE_URL}$imagePath"
            }
        } ?: emptyList()
}
