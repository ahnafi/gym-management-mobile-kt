package com.triosalak.gymmanagement.data.model.request

import com.google.gson.annotations.SerializedName

// $validator = Validator::make($request->all(), [
//            'current_password' => 'required|string',
//            'new_password' => 'required|string|min:8|confirmed',
//        ]);

data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)
