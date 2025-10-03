package com.triosalak.gymmanagement.data.model.request

import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.utils.MultipartUtils
import okhttp3.MultipartBody

/*
    'name' => 'sometimes|string|max:255',
    'phone' => 'nullable|string|max:20',
    'profile_bio' => 'nullable|string|max:1000',
    'profile_image' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
 */
data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profile_bio")
    val bio: String? = null,
    @SerializedName("phone")
    val phone: String? = null
)

data class UpdatePhotoProfileRequest(
    @SerializedName("profile_image")
    val photo: MultipartBody.Part
)

//data class UpdatePasswordRequest(
//    @SerializedName("current_password")
//    val currentPassword: String,
//    @SerializedName("new_password")
//    val newPassword: String,
//    @SerializedName("confirm_password")
//    val confirmPassword: String
//)