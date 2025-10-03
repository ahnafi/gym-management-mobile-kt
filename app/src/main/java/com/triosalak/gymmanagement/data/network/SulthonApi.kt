package com.triosalak.gymmanagement.data.network

import androidx.datastore.preferences.protobuf.Method
import com.google.gson.annotations.SerializedName
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.request.UpdateProfileRequest
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
import com.triosalak.gymmanagement.data.model.response.UpdateProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

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

    @PUT("profile")
    suspend fun updateProfile(
        @Body updateProfileRequest: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST("profile")
    suspend fun updatePhotoProfile(
        @Part profileImage: MultipartBody.Part,
        @Part("_method") method: okhttp3.RequestBody
    ): Response<UpdateProfileResponse>
}