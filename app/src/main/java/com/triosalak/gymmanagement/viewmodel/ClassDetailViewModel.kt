package com.triosalak.gymmanagement.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.data.model.entity.GymClassSchedule
import com.triosalak.gymmanagement.data.model.request.InitiateGymClassPaymentRequest
import com.triosalak.gymmanagement.data.model.response.GetGymClassSchedulesResponse
import com.triosalak.gymmanagement.data.model.response.InitiateGymClassPaymentResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class ClassDetailViewModel(private val api: SulthonApi) : ViewModel() {

    private val _gymClass = MutableLiveData<GymClass?>()
    val gymClass: MutableLiveData<GymClass?> = _gymClass

    private val _gymClassSchedules = MutableLiveData<List<GymClassSchedule>>()
    val gymClassSchedules: MutableLiveData<List<GymClassSchedule>> = _gymClassSchedules

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isLoadingSchedules = MutableLiveData<Boolean>()
    val isLoadingSchedules: MutableLiveData<Boolean> = _isLoadingSchedules

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private val _paymentInitiated = MutableLiveData<InitiateGymClassPaymentResponse?>()
    val paymentInitiated: MutableLiveData<InitiateGymClassPaymentResponse?> = _paymentInitiated

    private val _isInitiatingPayment = MutableLiveData<Boolean>()
    val isInitiatingPayment: MutableLiveData<Boolean> = _isInitiatingPayment

    fun getGymClassDetail(classId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.getGymClassDetail(classId)
                if (response.isSuccessful) {
                    _gymClass.value = response.body()?.data
                } else {
                    _errorMessage.value = "Failed to load class details"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGymClassSchedules(
        classId: Int,
        startDate: String? = null,
        endDate: String? = null,
        perPage: Int = 15
    ) {
        _isLoadingSchedules.value = true
        viewModelScope.launch {
            try {
                val response = api.getGymClassSchedules(classId, startDate, endDate, perPage)
                if (response.isSuccessful) {
                    val scheduleData = response.body()?.data
                    _gymClass.value = scheduleData?.gymClass
                    _gymClassSchedules.value = scheduleData?.schedules?.data ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load class schedules"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoadingSchedules.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun initiateGymClassPayment(gymClassId: Int, gymClassScheduleId: Int) {
        _isInitiatingPayment.value = true
        viewModelScope.launch {
            try {
                val request = InitiateGymClassPaymentRequest(
                    gymClassId = gymClassId,
                    gymClassScheduleId = gymClassScheduleId
                )
                val response = api.initiateGymClassPayment(request)
                if (response.isSuccessful) {
                    _paymentInitiated.value = response.body()
                } else {
                    _errorMessage.value = "Failed to initiate payment"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isInitiatingPayment.value = false
            }
        }
    }

    fun clearPaymentInitiated() {
        _paymentInitiated.value = null
    }
}