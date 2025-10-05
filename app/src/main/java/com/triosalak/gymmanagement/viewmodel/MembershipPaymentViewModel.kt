package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.request.InitiateMembershipPaymentRequest
import com.triosalak.gymmanagement.data.model.response.InitiateMembershipPaymentResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class MembershipPaymentViewModel(private val apiService: SulthonApi) : ViewModel() {

    private val _paymentResult = MutableLiveData<Result<InitiateMembershipPaymentResponse>>()
    val paymentResult: LiveData<Result<InitiateMembershipPaymentResponse>> = _paymentResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun initiatePayment(membershipPackageId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = InitiateMembershipPaymentRequest(membershipPackageId)
                val response = apiService.initiateMembershipPayment(request)
                
                if (response.isSuccessful) {
                    response.body()?.let { paymentResponse ->
                        _paymentResult.value = Result.success(paymentResponse)
                    } ?: run {
                        _paymentResult.value = Result.failure(Exception("Response body is null"))
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Paket membership tidak tersedia"
                        422 -> "Data tidak valid"
                        500 -> "Terjadi kesalahan server"
                        else -> "Gagal memproses pembayaran (${response.code()})"
                    }
                    _paymentResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                _paymentResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}