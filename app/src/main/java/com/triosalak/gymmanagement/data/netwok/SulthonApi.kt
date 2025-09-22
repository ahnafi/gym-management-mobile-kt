package com.triosalak.gymmanagement.data.netwok

import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SulthonApi {
    @POST("/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("/register")
    suspend fun register(
        @Body registerRequest: Map<String, String>
    )

    @POST("/logout")
    suspend fun logout()

    @GET("/users")
    suspend fun getUsers(
        //
    )


}