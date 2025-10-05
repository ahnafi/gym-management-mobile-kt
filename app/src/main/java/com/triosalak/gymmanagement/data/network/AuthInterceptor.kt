package com.triosalak.gymmanagement.data.network

import android.util.Log
import com.triosalak.gymmanagement.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Use synchronous method to avoid coroutine issues
        val token = sessionManager.getAuthTokenSync()
        
        Log.d("AuthInterceptor", "Request URL: ${chain.request().url}")
        Log.d("AuthInterceptor", "Token retrieved: ${if (token != null) "Token exists (${token.take(20)}...)" else "No token found"}")

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d("AuthInterceptor", "Authorization header added")
        } ?: run {
            Log.w("AuthInterceptor", "No token available - proceeding without authorization")
        }

        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val request = requestBuilder.build()
        Log.d("AuthInterceptor", "Final request headers: ${request.headers}")
        
        return chain.proceed(request)
    }
}