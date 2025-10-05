package com.triosalak.gymmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triosalak.gymmanagement.data.model.entity.Transaction
import com.triosalak.gymmanagement.data.model.response.GetTransactionsResponse
import com.triosalak.gymmanagement.data.network.SulthonApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val api: SulthonApi
) : ViewModel() {

    companion object {
        private const val TAG = "TransactionViewModel"
    }

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Pagination
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _hasMorePages = MutableStateFlow(false)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    // Filter states
    private val _currentPaymentStatusFilter = MutableStateFlow<String?>(null)
    val currentPaymentStatusFilter: StateFlow<String?> = _currentPaymentStatusFilter.asStateFlow()

    private val _currentPurchasableTypeFilter = MutableStateFlow<String?>(null)
    val currentPurchasableTypeFilter: StateFlow<String?> = _currentPurchasableTypeFilter.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions(
        page: Int = 1,
        refresh: Boolean = false,
        paymentStatus: String? = null,
        purchasableType: String? = null
    ) {
        viewModelScope.launch {
            try {
                if (refresh || page == 1) {
                    _isLoading.value = true
                    _errorMessage.value = null
                }

                Log.d(TAG, "Loading transactions - Page: $page, PaymentStatus: $paymentStatus, PurchasableType: $purchasableType")

                val response = api.getTransactions(
                    page = page,
                    perPage = 15,
                    paymentStatus = paymentStatus,
                    purchasableType = purchasableType
                )

                if (response.isSuccessful && response.body() != null) {
                    val transactionResponse = response.body()!!
                    Log.d(TAG, "API Success - Status: ${transactionResponse.status}")

                    val newTransactions = transactionResponse.data.transactions
                    val paginationData = transactionResponse.data

                    // Update pagination info
                    _currentPage.value = paginationData.currentPage
                    _totalPages.value = paginationData.lastPage
                    _hasMorePages.value = paginationData.currentPage < paginationData.lastPage

                    // Update transactions list
                    if (page == 1 || refresh) {
                        // Replace existing transactions for first page or refresh
                        _transactions.value = newTransactions
                    } else {
                        // Append for pagination
                        _transactions.value = _transactions.value + newTransactions
                    }

                    // Update current filters
                    _currentPaymentStatusFilter.value = paymentStatus
                    _currentPurchasableTypeFilter.value = purchasableType

                    Log.d(TAG, "Loaded ${newTransactions.size} transactions. Total in list: ${_transactions.value.size}")

                } else {
                    val errorMsg = "Failed to load transactions: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    _errorMessage.value = errorMsg
                }

            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.localizedMessage}"
                Log.e(TAG, errorMsg, e)
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreTransactions() {
        if (!_isLoading.value && _hasMorePages.value) {
            val nextPage = _currentPage.value + 1
            loadTransactions(
                page = nextPage,
                paymentStatus = _currentPaymentStatusFilter.value,
                purchasableType = _currentPurchasableTypeFilter.value
            )
        }
    }

    fun refreshTransactions() {
        loadTransactions(
            page = 1,
            refresh = true,
            paymentStatus = _currentPaymentStatusFilter.value,
            purchasableType = _currentPurchasableTypeFilter.value
        )
    }

    fun filterByPaymentStatus(status: String?) {
        Log.d(TAG, "Filtering by payment status: $status")
        loadTransactions(
            page = 1,
            refresh = true,
            paymentStatus = status,
            purchasableType = _currentPurchasableTypeFilter.value
        )
    }

    fun filterByPurchasableType(type: String?) {
        Log.d(TAG, "Filtering by purchasable type: $type")
        loadTransactions(
            page = 1,
            refresh = true,
            paymentStatus = _currentPaymentStatusFilter.value,
            purchasableType = type
        )
    }

    fun clearFilters() {
        Log.d(TAG, "Clearing all filters")
        loadTransactions(page = 1, refresh = true)
    }

    fun selectTransaction(transaction: Transaction) {
        Log.d(TAG, "Selecting transaction: ${transaction.code}")
        _selectedTransaction.value = transaction
    }

    fun clearSelectedTransaction() {
        _selectedTransaction.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Helper function to get transaction by ID (useful for navigation)
    fun getTransactionById(transactionId: Int): Transaction? {
        return _transactions.value.find { it.id == transactionId }
    }

    // Helper function to check if current filter is active
    fun isFilterActive(filterType: String, filterValue: String?): Boolean {
        return when (filterType) {
            "payment_status" -> _currentPaymentStatusFilter.value == filterValue
            "purchasable_type" -> _currentPurchasableTypeFilter.value == filterValue
            else -> false
        }
    }
}