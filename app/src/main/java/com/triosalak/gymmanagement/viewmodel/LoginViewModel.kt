package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.netwok.SulthonApi
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(
    private val api: SulthonApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        Log.d("LOGIN_ATTEMPT", "Attempting login for email: $email with password: $password")

        viewModelScope.launch {
            try {
                val response = api.login(loginRequest)

                Log.d("LOGIN_RESPONSE", "Response: ${response.raw()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()!!
                    val token = loginResponse.data.token
                    Log.d("LOGIN_SUCCESS", "Token: $token")

                    sessionManager.saveAuthToken(token)
                    _loginResult.value = Result.success(loginResponse)
                } else {
                    Log.e("LOGIN_FAILED", "Error: ${response.code()} - ${response.body()}")
                    _loginResult.value = Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Error: ${e.localizedMessage}")
                _loginResult.value = Result.failure(e)
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

