package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.response.GetMembershipPackagesResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class MembershipPackagesViewModel(private val api: SulthonApi) : ViewModel() {

    private val _membershipPackages = MutableLiveData<Result<GetMembershipPackagesResponse>>()
    val membershipPackages: LiveData<Result<GetMembershipPackagesResponse>> = _membershipPackages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchMembershipPackages() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                android.util.Log.d("MembershipPackages", "Starting API call...")
                val response = api.getMembershipPackages()
                
                android.util.Log.d("MembershipPackages", "Response received - isSuccessful: ${response.isSuccessful}")
                android.util.Log.d("MembershipPackages", "Response code: ${response.code()}")
                android.util.Log.d("MembershipPackages", "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    val membershipPackagesResponse = response.body()
                    android.util.Log.d("MembershipPackages", "Response body: $membershipPackagesResponse")
                    
                    if (membershipPackagesResponse != null) {
                        android.util.Log.d("MembershipPackages", "Status: ${membershipPackagesResponse.status}")
                        if (membershipPackagesResponse.status == "success") {
                            android.util.Log.d("MembershipPackages", "Data found: ${membershipPackagesResponse.data.data.size} packages")
                            _membershipPackages.value = Result.success(membershipPackagesResponse)
                        } else {
                            android.util.Log.e("MembershipPackages", "API returned non-success status: ${membershipPackagesResponse.status}")
                            _membershipPackages.value = Result.failure(Exception("Server returned error status: ${membershipPackagesResponse.status}"))
                        }
                    } else {
                        android.util.Log.e("MembershipPackages", "Response body is null")
                        _membershipPackages.value =
                            Result.failure(Exception("Failed to fetch Membership Packages: Empty response"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "HTTP ${response.code()}: ${response.message()}. Body: $errorBody"
                    android.util.Log.e("MembershipPackages", "API Error: $errorMessage")
                    _membershipPackages.value =
                        Result.failure(Exception("Failed to fetch membership packages: $errorMessage"))
                }
            } catch (e: Exception) {
                android.util.Log.e("MembershipPackages", "Exception occurred: ${e.message}", e)
                android.util.Log.e("MembershipPackages", "Exception type: ${e::class.java.simpleName}")
                _membershipPackages.value = Result.failure(e)
            } finally {
                _isLoading.value = false
                android.util.Log.d("MembershipPackages", "fetchMembershipPackages() completed")
            }
        }
    }
}