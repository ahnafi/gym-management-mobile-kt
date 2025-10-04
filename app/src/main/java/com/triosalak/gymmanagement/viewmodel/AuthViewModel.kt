package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
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

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                val response = api.resendVerificationEmail()
                if (response.isSuccessful) {
                    Log.d("RESEND_VERIFICATION", "Verification email resent successfully.")
                } else {
                    Log.e(
                        "RESEND_VERIFICATION_FAILED",
                        "Error: ${response.code()} - ${response.message()}"
                    )

                    throw Exception("Resend verification failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Error: ${e.localizedMessage}", e)
            }
        }
    }

    suspend fun logout() {
        try {
            api.logout()
        } finally {
            sessionManager.clearAuthToken()
        }
    }
}
