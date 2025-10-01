package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.response.CurrentUserResponse
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
import com.triosalak.gymmanagement.data.model.response.ResendVerificationResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val api: SulthonApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    // Add LiveData for register result
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    // Add LiveData for email verification result
    private val _resendVerificationResult = MutableLiveData<Result<ResendVerificationResponse>>()
    val resendVerificationResult: LiveData<Result<ResendVerificationResponse>> =
        _resendVerificationResult

    // Add LiveData for email verification status
    private val _emailVerificationStatus = MutableLiveData<Boolean>()
    val emailVerificationStatus: LiveData<Boolean> = _emailVerificationStatus

    // Add LiveData for current user result
    private val _currentUserResult = MutableLiveData<Result<CurrentUserResponse>>()
    val currentUserResult: LiveData<Result<CurrentUserResponse>> = _currentUserResult

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        Log.d("LOGIN_ATTEMPT", "Attempting login for email: $email with password: $password")

        viewModelScope.launch {
            try {
                val response = api.login(loginRequest)

                Log.d("LOGIN_RESPONSE", "Response: ${response.raw()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val token = loginResponse.data.token
                        val user = loginResponse.data.user
                        Log.d("LOGIN_SUCCESS", "Token: $token")
                        Log.d("LOGIN_SUCCESS", "User: ${user.name} - ${user.email}")

                        // Simpan token dan user data
                        sessionManager.saveAuthToken(token)
                        sessionManager.saveCurrentUser(user)

                        _loginResult.value = Result.success(loginResponse)
                    } else {
                        Log.e("LOGIN_FAILED", "Response body is null")
                        _loginResult.value =
                            Result.failure(Exception("Login failed: Empty response"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOGIN_FAILED", "Error: ${response.code()} - $errorBody")
                    _loginResult.value =
                        Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Error: ${e.localizedMessage}", e)
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        val registerRequest = RegisterRequest(name, email, password, passwordConfirmation)

        Log.d("REGISTER_ATTEMPT", "Attempting register for email: $email")

        viewModelScope.launch {
            try {
                val response = api.register(registerRequest)

                Log.d("REGISTER_RESPONSE", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        Log.d(
                            "REGISTER_SUCCESS",
                            "User registered: ${registerResponse.data.user.email}"
                        )
                        _registerResult.value = Result.success(registerResponse)

                        // Optionally, you can log the user in immediately after registration
                        login(email, password)
                    } else {
                        Log.e("REGISTER_FAILED", "Response body is null")
                        _registerResult.value =
                            Result.failure(Exception("Registration failed: Empty response"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("REGISTER_FAILED", "Error: ${response.code()} - $errorBody")
                    _registerResult.value =
                        Result.failure(Exception("Registration failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Error: ${e.localizedMessage}", e)
                _registerResult.value = Result.failure(e)
            }
        }
    }

    suspend fun logout() {
        try {
            api.logout()
        } finally {
            sessionManager.clearAllData()
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            try {
                val response = api.resendVerification()

                Log.d("RESEND_VERIFICATION_RESPONSE", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val resendResponse = response.body()
                    if (resendResponse != null) {
                        Log.d("RESEND_VERIFICATION_SUCCESS", "Message: ${resendResponse.message}")
                        _resendVerificationResult.value = Result.success(resendResponse)
                    } else {
                        Log.e("RESEND_VERIFICATION_FAILED", "Response body is null")
                        _resendVerificationResult.value =
                            Result.failure(Exception("Resend verification failed: Empty response"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RESEND_VERIFICATION_FAILED", "Error: ${response.code()} - $errorBody")

                    // Handle specific HTTP error codes
                    val errorMessage = when (response.code()) {
                        400 -> "Email sudah diverifikasi"
                        401 -> "Unauthorized: Silakan login ulang"
                        403 -> "Forbidden: Email sudah diverifikasi atau akun tidak ditemukan"
                        404 -> "Endpoint tidak ditemukan"
                        422 -> "Data tidak valid"
                        500 -> "Server error: Coba lagi nanti"
                        else -> "Network error (${response.code()}): ${response.message()}"
                    }

                    Log.d("RESEND_VERIFICATION_ERROR", errorMessage)

                    _resendVerificationResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: com.google.gson.JsonSyntaxException) {
                Log.e("NETWORK_ERROR", "JSON parsing error: ${e.localizedMessage}", e)
                _resendVerificationResult.value =
                    Result.failure(Exception("Server mengembalikan response yang tidak valid. Coba lagi nanti."))
            } catch (e: java.net.UnknownHostException) {
                Log.e("NETWORK_ERROR", "Network error: ${e.localizedMessage}", e)
                _resendVerificationResult.value =
                    Result.failure(Exception("Tidak dapat terhubung ke server. Periksa koneksi internet Anda."))
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("NETWORK_ERROR", "Timeout error: ${e.localizedMessage}", e)
                _resendVerificationResult.value =
                    Result.failure(Exception("Request timeout. Coba lagi nanti."))
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Error: ${e.localizedMessage}", e)
                _resendVerificationResult.value =
                    Result.failure(Exception("Terjadi kesalahan: ${e.localizedMessage}"))
            }
        }
    }

    // Method untuk mengecek status verifikasi email
    fun checkEmailVerificationStatus() {
        viewModelScope.launch {
            val isVerified = sessionManager.isEmailVerified()
            _emailVerificationStatus.value = isVerified
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val response = api.getCurrentUser()

                if (response.isSuccessful) {
                    val currentUserResponse = response.body()
                    if (currentUserResponse != null) {
                        val user = currentUserResponse.data // Extract user from response
                        Log.d("GET_CURRENT_USER", "User: ${user.name} - ${user.email}")
                        Log.d("GET_CURRENT_USER", "Email verified: ${user.emailVerifiedAt}")

                        // Update user data di session
                        sessionManager.saveCurrentUser(user)
                        _currentUserResult.value = Result.success(currentUserResponse)

                        // Update email verification status
                        _emailVerificationStatus.value = user.emailVerifiedAt != null
                    } else {
                        Log.e("GET_CURRENT_USER", "Response body is null")
                        _currentUserResult.value = Result.failure(Exception("User not found"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("GET_CURRENT_USER", "Error: ${response.code()} - $errorBody")

                    val errorMessage = when (response.code()) {
                        401 -> "Unauthorized: Silakan login ulang"
                        403 -> "Access forbidden"
                        404 -> "User not found"
                        500 -> "Server error: Coba lagi nanti"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }

                    _currentUserResult.value = Result.failure(Exception(errorMessage))
                }

            } catch (e: com.google.gson.JsonSyntaxException) {
                Log.e("GET_CURRENT_USER", "JSON parsing error: ${e.localizedMessage}", e)
                _currentUserResult.value = Result.failure(Exception("Server mengembalikan response yang tidak valid"))
            } catch (e: java.net.UnknownHostException) {
                Log.e("GET_CURRENT_USER", "Network error: ${e.localizedMessage}", e)
                _currentUserResult.value = Result.failure(Exception("Tidak dapat terhubung ke server"))
            } catch (e: Exception) {
                Log.e("GET_CURRENT_USER", "Error fetching current user: ${e.localizedMessage}", e)
                _currentUserResult.value = Result.failure(Exception("Terjadi kesalahan: ${e.localizedMessage}"))
            }
        }
    }

}
