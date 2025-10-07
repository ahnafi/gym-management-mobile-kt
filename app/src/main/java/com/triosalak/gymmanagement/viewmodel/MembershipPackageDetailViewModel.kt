package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.response.GetMembershipPackageResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class MembershipPackageDetailViewModel(private val api: SulthonApi) : ViewModel() {

    private val _packageDetail = MutableLiveData<Result<GetMembershipPackageResponse>>()
    val packageDetail: LiveData<Result<GetMembershipPackageResponse>> = _packageDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchPackageDetail(packageId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.getMembershipPackageDetail(packageId)

                if (response.isSuccessful) {
                    val packageDetailResponse = response.body()
                    if (packageDetailResponse != null) {
                        _packageDetail.value = Result.success(packageDetailResponse)
                    } else {
                        _packageDetail.value =
                            Result.failure(Exception("Failed to fetch package detail: Empty response"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "HTTP ${response.code()}: ${response.message()}. Body: $errorBody"
                    _packageDetail.value =
                        Result.failure(Exception("Failed to fetch package detail: $errorMessage"))
                }
            } catch (e: Exception) {
                _packageDetail.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}