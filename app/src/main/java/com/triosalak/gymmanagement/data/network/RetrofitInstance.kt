package com.triosalak.gymmanagement.data.network

import android.util.Log
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    fun getApiService(sessionManager: SessionManager): SulthonApi {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = AuthInterceptor(sessionManager)

        Log.d("AuthInterceptor", "Token: ${sessionManager.getAuthTokenSync()}")

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        // Configure Gson with lenient mode to handle malformed JSON
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(SulthonApi::class.java)
    }
}