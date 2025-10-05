package com.triosalak.gymmanagement.data.network

import com.triosalak.gymmanagement.data.model.request.ChangePasswordRequest
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.request.UpdateProfileRequest
import com.triosalak.gymmanagement.data.model.response.ChangePasswordResponse
import com.triosalak.gymmanagement.data.model.response.GetCurrentUserResponse
import com.triosalak.gymmanagement.data.model.response.GetMyStatisticResponse
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
import com.triosalak.gymmanagement.data.model.response.ResendVerificationEmailResponse
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

    @GET("profile")
    suspend fun getCurrentUser(): Response<GetCurrentUserResponse>

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

    @GET("gym-classes")
    suspend fun getGymClasses(): Response<>

    @POST("resend-verification")
    suspend fun resendVerificationEmail(): Response<ResendVerificationEmailResponse>

    @POST("change-password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @GET("gym-visits/statistics/my-stats")
    suspend fun getMyStatistic(): Response<GetMyStatisticResponse>
}