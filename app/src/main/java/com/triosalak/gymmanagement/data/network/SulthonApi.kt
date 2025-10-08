package com.triosalak.gymmanagement.data.network

import com.triosalak.gymmanagement.data.model.request.ChangePasswordRequest
import com.triosalak.gymmanagement.data.model.request.LoginRequest
import com.triosalak.gymmanagement.data.model.request.RegisterRequest
import com.triosalak.gymmanagement.data.model.request.UpdateProfileRequest
import com.triosalak.gymmanagement.data.model.request.InitiateMembershipPaymentRequest
import com.triosalak.gymmanagement.data.model.request.InitiateGymClassPaymentRequest
import com.triosalak.gymmanagement.data.model.response.ChangePasswordResponse
import com.triosalak.gymmanagement.data.model.response.InitiateMembershipPaymentResponse
import com.triosalak.gymmanagement.data.model.response.GetCurrentUserResponse
import com.triosalak.gymmanagement.data.model.response.GetGymClassDetailResponse
import com.triosalak.gymmanagement.data.model.response.GetGymClassesResponse
import com.triosalak.gymmanagement.data.model.response.GetGymClassSchedulesResponse
import com.triosalak.gymmanagement.data.model.response.GetMyStatisticResponse
import com.triosalak.gymmanagement.data.model.response.GetMembershipPackageResponse
import com.triosalak.gymmanagement.data.model.response.GetMembershipPackagesResponse
import com.triosalak.gymmanagement.data.model.response.GetTransactionsResponse
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.model.response.RegisterResponse
import com.triosalak.gymmanagement.data.model.response.ResendVerificationEmailResponse
import com.triosalak.gymmanagement.data.model.response.UpdateProfileResponse
import com.triosalak.gymmanagement.data.model.response.GetMyMembershipsResponse
import com.triosalak.gymmanagement.data.model.response.InitiateGymClassPaymentResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getGymClasses(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 6
    ): Response<GetGymClassesResponse>

    @GET("gym-classes/{id}")
    suspend fun getGymClassDetail(
        @Path("id") classId: Int?
    ): Response<GetGymClassDetailResponse>

    @POST("resend-verification")
    suspend fun resendVerificationEmail(): Response<ResendVerificationEmailResponse>

    @POST("change-password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @GET("gym-visits/statistics/my-stats")
    suspend fun getMyStatistic(): Response<GetMyStatisticResponse>

    @GET("membership/packages")
    suspend fun getMembershipPackages(): Response<GetMembershipPackagesResponse>

    @GET("membership/packages/{id}")
    suspend fun getMembershipPackageDetail(
        @retrofit2.http.Path("id") packageId: Int
    ): Response<GetMembershipPackageResponse>

    @GET("payments/my-transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15,
        @Query("payment_status") paymentStatus: String? = null,
        @Query("purchasable_type") purchasableType: String? = null
    ): Response<GetTransactionsResponse>

    @GET("payments/transactions/{id}")
    suspend fun getTransactionDetail(
        @retrofit2.http.Path("id") transactionId: Int
    ): Response<GetTransactionsResponse>

    @POST("payments/membership")
    suspend fun initiateMembershipPayment(
        @Body initiateMembershipPaymentRequest: InitiateMembershipPaymentRequest
    ): Response<InitiateMembershipPaymentResponse>

    @GET("membership/my-memberships")
    suspend fun getMyMemberships(): Response<GetMyMembershipsResponse>

    @GET("gym-classes/{classId}/schedules")
    suspend fun getGymClassSchedules(
        @retrofit2.http.Path("classId") classId: Int,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("per_page") perPage: Int = 15
    ): Response<GetGymClassSchedulesResponse>

    @POST("payments/gym-class")
    suspend fun initiateGymClassPayment(
        @Body initiateGymClassPaymentRequest: InitiateGymClassPaymentRequest
    ): Response<InitiateGymClassPaymentResponse>
}