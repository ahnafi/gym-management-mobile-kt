package com.triosalak.gymmanagement.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.request.UpdateProfileRequest
import com.triosalak.gymmanagement.data.network.SulthonApi
import com.triosalak.gymmanagement.utils.MultipartUtils
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val api: SulthonApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateResult = MutableStateFlow<UpdateResult?>(null)
    val updateResult: StateFlow<UpdateResult?> = _updateResult

    fun updateProfile(name: String?, phone: String?, bio: String?) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "=== Starting Profile Text Update ===")
                Log.d(TAG, "Input - Name: '${name}', Phone: '${phone}', Bio: '${bio}'")

                _isLoading.value = true
                _updateResult.value = null

                // Get current user before update - MUST exist
                val currentUser = sessionManager.getCurrentUserSync()
                if (currentUser == null) {
                    Log.e(TAG, "❌ No current user found in session, cannot update profile")
                    _updateResult.value =
                        UpdateResult.Error("Sesi tidak valid, silakan login kembali")
                    return@launch
                }

                Log.d(
                    TAG,
                    "Current user before update: ID:${currentUser.id}, Name:${currentUser.name}, Phone:${currentUser.phone}, Bio:${currentUser.profileBio}"
                )

                val request = UpdateProfileRequest(
                    name = name?.takeIf { it.isNotBlank() },
                    phone = phone?.takeIf { it.isNotBlank() },
                    bio = bio?.takeIf { it.isNotBlank() }
                )

                Log.d(
                    TAG,
                    "API Request created: name=${request.name}, phone=${request.phone}, bio=${request.bio}"
                )
                Log.i(TAG, "Calling API updateProfile endpoint...")

                val response = api.updateProfile(request)

                Log.d(
                    TAG,
                    "API Response - Success: ${response.isSuccessful}, Code: ${response.code()}"
                )
                if (!response.isSuccessful) {
                    Log.e(
                        TAG,
                        "API Error - Message: ${response.message()}, ErrorBody: ${
                            response.errorBody()?.string()
                        }"
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    val updateResponse = response.body()!!
                    Log.i(
                        TAG,
                        "API Success - Status: ${updateResponse.status}, Message: ${updateResponse.message}"
                    )

                    // Log the entire response structure for debugging
                    Log.d(TAG, "Full API Response: $updateResponse")
                    Log.d(TAG, "Response data (User): ${updateResponse.data}")

                    val updatedUser = updateResponse.data

                    Log.d(
                        TAG,
                        "Data from API response: ID:${updatedUser.id}, Name:${updatedUser.name}, Phone:${updatedUser.phone}, Bio:${updatedUser.profileBio}"
                    )

                    // Merge the data - prioritize API response but preserve important fields
                    val mergedUser = currentUser.copy(
                        name = updatedUser.name ?: currentUser.name,
                        phone = updatedUser.phone ?: currentUser.phone,
                        profileBio = updatedUser.profileBio ?: currentUser.profileBio,
                        profileImage = updatedUser.profileImage ?: currentUser.profileImage,
                        // Preserve critical fields that might not be in update response
                        email = currentUser.email,
                        role = updatedUser.role ?: currentUser.role,
                        membershipStatus = updatedUser.membershipStatus
                            ?: currentUser.membershipStatus,
                        membershipEndDate = updatedUser.membershipEndDate
                            ?: currentUser.membershipEndDate,
                        emailVerifiedAt = currentUser.emailVerifiedAt,
                        createdAt = currentUser.createdAt,
                        updatedAt = updatedUser.updatedAt ?: currentUser.updatedAt,
                        id = currentUser.id ?: updatedUser.id
                    )

                    Log.d(
                        TAG,
                        "Merged user data: ID:${mergedUser.id}, Name:${mergedUser.name}, Phone:${mergedUser.phone}, Bio:${mergedUser.profileBio}"
                    )
                    Log.i(TAG, "Saving merged user data to SessionManager...")

                    // Save the merged user data
                    sessionManager.saveCurrentUser(mergedUser)
                    Log.i(TAG, "✅ Profile text update completed successfully")
                    _updateResult.value = UpdateResult.Success("Profile berhasil diperbarui")
                } else {
                    val errorMsg = "Gagal memperbarui profile: ${response.message()}"
                    Log.e(TAG, "❌ $errorMsg")
                    _updateResult.value = UpdateResult.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                Log.e(TAG, "❌ Exception during profile update: ${e.message}", e)
                Log.e(TAG, "Exception stacktrace: ", e)
                _updateResult.value = UpdateResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "=== Profile Text Update Finished ===")
            }
        }
    }

    fun updatePhotoProfile(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "=== Starting Profile Photo Update ===")
                Log.d(TAG, "Image URI: $imageUri")

                _isLoading.value = true
                _updateResult.value = null

                // Get current user before update - MUST exist
                val currentUser = sessionManager.getCurrentUserSync()
                if (currentUser == null) {
                    Log.e(TAG, "❌ No current user found in session, cannot update photo")
                    _updateResult.value =
                        UpdateResult.Error("Sesi tidak valid, silakan login kembali")
                    return@launch
                }

                Log.d(
                    TAG,
                    "Current user before photo update: ID:${currentUser.id}, Name:${currentUser.name}, ProfileImage:${currentUser.profileImage}"
                )

                Log.i(TAG, "Creating multipart image data...")
                val imagePart = MultipartUtils.createPartFromUri(
                    context = context,
                    fieldName = "profile_image",
                    fileName = "profile_${System.currentTimeMillis()}.jpg",
                    uri = imageUri
                )

                if (imagePart == null) {
                    Log.e(TAG, "❌ Failed to create multipart data from URI: $imageUri")
                    _updateResult.value = UpdateResult.Error("Gagal memproses gambar")
                    return@launch
                }

                Log.d(
                    TAG,
                    "Multipart created - Field: ${imagePart.body.contentType()}, Size: ${imagePart.body.contentLength()} bytes"
                )
                Log.i(TAG, "Calling API updatePhotoProfile endpoint...")

                val response = api.updatePhotoProfile(
                    imagePart,
                    MultipartUtils.createPartFromString("PUT")
                )

                Log.d(
                    TAG,
                    "API Response - Success: ${response.isSuccessful}, Code: ${response.code()}"
                )
                if (!response.isSuccessful) {
                    Log.e(
                        TAG,
                        "API Error - Message: ${response.message()}, ErrorBody: ${
                            response.errorBody()?.string()
                        }"
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    val updateResponse = response.body()!!
                    Log.i(
                        TAG,
                        "API Success - Status: ${updateResponse.status}, Message: ${updateResponse.message}"
                    )

                    // Log the entire response structure for debugging
                    Log.d(TAG, "Full API Response: $updateResponse")
                    Log.d(TAG, "Response data (User): ${updateResponse.data}")

                    val updatedUser = updateResponse.data

                    Log.d(
                        TAG,
                        "Data from API response: ID:${updatedUser.id}, Name:${updatedUser.name}, ProfileImage:${updatedUser.profileImage}"
                    )

                    // Merge the data - prioritize API response but preserve all important fields
                    val mergedUser = currentUser.copy(
                        name = updatedUser.name ?: currentUser.name,
                        phone = updatedUser.phone ?: currentUser.phone,
                        profileBio = updatedUser.profileBio ?: currentUser.profileBio,
                        profileImage = updatedUser.profileImage ?: currentUser.profileImage,
                        // Preserve critical fields
                        email = currentUser.email,
                        role = updatedUser.role ?: currentUser.role,
                        membershipStatus = updatedUser.membershipStatus
                            ?: currentUser.membershipStatus,
                        membershipEndDate = updatedUser.membershipEndDate
                            ?: currentUser.membershipEndDate,
                        emailVerifiedAt = currentUser.emailVerifiedAt,
                        createdAt = currentUser.createdAt,
                        updatedAt = updatedUser.updatedAt ?: currentUser.updatedAt,
                        id = currentUser.id ?: updatedUser.id
                    )

                    Log.d(
                        TAG,
                        "Merged user data: ID:${mergedUser.id}, Name:${mergedUser.name}, ProfileImage:${mergedUser.profileImage}"
                    )
                    Log.d(
                        TAG,
                        "All preserved fields - Email:${mergedUser.email}, Role:${mergedUser.role}, Membership:${mergedUser.membershipStatus}"
                    )
                    Log.i(TAG, "Saving merged user data to SessionManager...")

                    // Save the merged user data
                    sessionManager.saveCurrentUser(mergedUser)
                    Log.i(TAG, "✅ Profile photo update completed successfully")
                    _updateResult.value = UpdateResult.Success("Foto profile berhasil diperbarui")
                } else {
                    val errorMsg = "Gagal memperbarui foto: ${response.message()}"
                    Log.e(TAG, "❌ $errorMsg")
                    _updateResult.value = UpdateResult.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                Log.e(TAG, "❌ Exception during photo update: ${e.message}", e)
                Log.e(TAG, "Exception stacktrace: ", e)
                _updateResult.value = UpdateResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "=== Profile Photo Update Finished ===")
            }
        }
    }

    fun clearResult() {
        Log.d(TAG, "Clearing update result")
        _updateResult.value = null
    }

    sealed class UpdateResult {
        data class Success(val message: String) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}