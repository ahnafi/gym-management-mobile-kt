package com.triosalak.gymmanagement.data.network

import com.triosalak.gymmanagement.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Use synchronous method to avoid coroutine issues
        val token = sessionManager.getAuthTokenSync()

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}