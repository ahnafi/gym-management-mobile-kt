package com.triosalak.gymmanagement.data.network

import com.google.gson.GsonBuilder
import com.triosalak.gymmanagement.data.model.deserializer.PurchasableDeserializer
import com.triosalak.gymmanagement.data.model.entity.Purchasable
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

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        // Create custom Gson instance with PurchasableDeserializer
        val gson = GsonBuilder()
            .registerTypeAdapter(Purchasable::class.java, PurchasableDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(SulthonApi::class.java)
    }
}