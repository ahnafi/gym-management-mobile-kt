package com.triosalak.gymmanagement.data.model.entity

import com.google.gson.annotations.SerializedName

// Interface for polymorphic purchasable items
interface Purchasable {
    val id: Int?
    val name: String?
    val price: Int?
}

// Extended MembershipPackage that implements Purchasable
data class PurchasableMembershipPackage(
    @SerializedName("id")
    override val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    override val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("duration")
    val duration: Int? = null,
    @SerializedName("price")
    override val price: Int? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("images")
    val images: List<String>? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Purchasable

// Extended GymClass that implements Purchasable
data class PurchasableGymClass(
    @SerializedName("id")
    override val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    override val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    override val price: Int? = null,
    @SerializedName("images")
    val images: List<String>? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Purchasable