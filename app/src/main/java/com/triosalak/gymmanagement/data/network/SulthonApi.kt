package com.triosalak.gymmanagement.data.network

import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SulthonApi {
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>

    @POST("logout")
    suspend fun logout()

    @GET("users")
    suspend fun getUsers(
        //
    )


}