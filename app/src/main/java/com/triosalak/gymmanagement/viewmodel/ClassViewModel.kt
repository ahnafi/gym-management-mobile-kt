package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.launch

class ClassViewModel(private val api: SulthonApi) : ViewModel() {

    private val _gymClasses = MutableLiveData<List<GymClass>>(emptyList())
    val gymClasses: LiveData<List<GymClass>> get() = _gymClasses

    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    fun getGymClass(page: Int = 1, append: Boolean = false) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = api.getGymClasses(page)
                if (response.isSuccessful) {
                    val body = response.body()?.data
                    body?.let {
                        currentPage = it.currentPage
                        lastPage = it.lastPage

                        val newList = if (append) {
                            val oldList = _gymClasses.value ?: emptyList()
                            oldList + it.classes
                        } else {
                            it.classes
                        }

                        _gymClasses.postValue(newList)
                    }
                }
            } catch (e: Exception) {
                Log.e("ClassViewModel", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun canLoadMore(): Boolean = currentPage < lastPage
    fun getNextPage(): Int = currentPage + 1
}