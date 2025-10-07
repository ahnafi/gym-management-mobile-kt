package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.response.GetMyStatisticResponse
import com.triosalak.gymmanagement.data.model.response.GetMyMembershipsResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class DashboardViewModel(private val api: SulthonApi) : ViewModel() {

    private val _myStatistic = MutableLiveData<Result<GetMyStatisticResponse>>()
    val myStatistic: LiveData<Result<GetMyStatisticResponse>> = _myStatistic

    private val _myMemberships = MutableLiveData<Result<GetMyMembershipsResponse>>()
    val myMemberships: LiveData<Result<GetMyMembershipsResponse>> = _myMemberships

    fun getStatistic() {
        viewModelScope.launch {
            val response = api.getMyStatistic()

            if (response.isSuccessful) {
                val statisticResponse = response.body()
                if (statisticResponse != null) {
                    _myStatistic.value = Result.success(statisticResponse)
                } else {
                    _myStatistic.value =
                        Result.failure(Exception("Failed to fetch statistics: Empty response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                _myStatistic.value =
                    Result.failure(Exception("Failed to fetch statistics: $errorBody"))
            }

        }
    }

    fun getMyMemberships() {
        viewModelScope.launch {
            val response = api.getMyMemberships()

            if (response.isSuccessful) {
                val membershipsResponse = response.body()
                if (membershipsResponse != null) {
                    _myMemberships.value = Result.success(membershipsResponse)
                } else {
                    _myMemberships.value =
                        Result.failure(Exception("Failed to fetch memberships: Empty response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                _myMemberships.value =
                    Result.failure(Exception("Failed to fetch memberships: $errorBody"))
            }
        }
    }
}