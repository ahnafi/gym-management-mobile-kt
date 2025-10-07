package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.entity.Transaction
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailViewModel(
    private val api: SulthonApi
) : ViewModel() {

    companion object {
        private const val TAG = "TransactionDetailViewModel"
    }

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadTransactionDetail(transactionId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                Log.d(TAG, "Loading transaction detail for ID: $transactionId")

                // For now, we'll load all transactions and find the specific one
                // This could be optimized with a dedicated API endpoint for single transaction
                val response = api.getTransactions(page = 1, perPage = 100)

                if (response.isSuccessful && response.body() != null) {
                    val transactionResponse = response.body()!!
                    Log.d(TAG, "API Success - Status: ${transactionResponse.status}")

                    val foundTransaction = transactionResponse.data.transactions.find { it.id == transactionId }
                    
                    if (foundTransaction != null) {
                        _transaction.value = foundTransaction
                        Log.d(TAG, "Transaction found: ${foundTransaction.code}")
                    } else {
                        val errorMsg = "Transaction with ID $transactionId not found"
                        Log.e(TAG, errorMsg)
                        _errorMessage.value = "Transaksi tidak ditemukan"
                    }
                } else {
                    val errorMsg = "Failed to load transaction: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    _errorMessage.value = "Gagal memuat detail transaksi"
                }

            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.localizedMessage}"
                Log.e(TAG, errorMsg, e)
                _errorMessage.value = "Terjadi kesalahan jaringan: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearTransaction() {
        _transaction.value = null
    }
}