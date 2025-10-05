package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.entity.MyStatistic
import com.triosalak.gymmanagement.data.model.response.GetMyStatisticResponse
import com.triosalak.gymmanagement.data.model.response.LoginResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class ClassViewModel(private val api: SulthonApi) : ViewModel() {

    private val _myStatistic = MutableLiveData<Result<GetMyStatisticResponse>>()
    val myStatistic: LiveData<Result<GetMyStatisticResponse>> = _myStatistic

    public fun getStatistic() {
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
}