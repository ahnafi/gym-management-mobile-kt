package com.triosalak.gymmanagement.ui.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentTransactionsBinding
import com.triosalak.gymmanagement.ui.transactions.adapter.TransactionAdapter
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var transactionAdapter: TransactionAdapter

    companion object {
        private const val TAG = "TransactionsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "=== TransactionsFragment onViewCreated ===")

        setupDependencies()
        setupRecyclerView()
        setupFilterButtons()
        observeViewModel()
        
        Log.i(TAG, "✅ TransactionsFragment setup completed")
    }

    private fun setupDependencies() {
        Log.d(TAG, "Setting up dependencies...")
        sessionManager = SessionManager(requireContext())
        
        val api = RetrofitInstance.getApiService(sessionManager)
        transactionViewModel = TransactionViewModel(api)
        Log.d(TAG, "Dependencies initialized successfully")
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView...")
        
        transactionAdapter = TransactionAdapter { transaction ->
            Log.d(TAG, "Transaction clicked: ${transaction.code}")
            transactionViewModel.selectTransaction(transaction)
            
            // Navigate to transaction detail
            val bundle = Bundle().apply {
                putInt("transactionId", transaction.id ?: 0)
            }
            findNavController().navigate(R.id.action_transactions_to_transactionDetail, bundle)
        }

        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            
            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    // Load more when near the end
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 3 && 
                        firstVisibleItemPosition >= 0) {
                        transactionViewModel.loadMoreTransactions()
                    }
                }
            })
        }
        
        Log.d(TAG, "RecyclerView setup completed")
    }

    private fun setupFilterButtons() {
        Log.d(TAG, "Setting up filter buttons...")
        
        with(binding) {
            btnFilterAll.setOnClickListener {
                Log.d(TAG, "Filter All clicked")
                transactionViewModel.clearFilters()
                updateFilterButtonStates("all")
            }
            
            btnFilterPending.setOnClickListener {
                Log.d(TAG, "Filter Pending clicked")
                transactionViewModel.filterByPaymentStatus("pending")
                updateFilterButtonStates("pending")
            }
            
            btnFilterPaid.setOnClickListener {
                Log.d(TAG, "Filter Paid clicked")
                transactionViewModel.filterByPaymentStatus("paid")
                updateFilterButtonStates("paid")
            }
        }
        
        Log.d(TAG, "Filter buttons setup completed")
    }

    private fun updateFilterButtonStates(activeFilter: String) {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.myred)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        
        with(binding) {
            // Reset all buttons to outline style
            listOf(btnFilterAll, btnFilterPending, btnFilterPaid).forEach { button ->
                button.setBackgroundColor(transparentColor)
                button.setTextColor(primaryColor)
                button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.myred)
            }
            
            // Set active button to filled style
            val activeButton = when (activeFilter) {
                "all" -> btnFilterAll
                "pending" -> btnFilterPending
                "paid" -> btnFilterPaid
                else -> btnFilterAll
            }
            
            activeButton.setBackgroundColor(primaryColor)
            activeButton.setTextColor(whiteColor)
            activeButton.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.myred)
        }
    }

    private fun observeViewModel() {
        Log.d(TAG, "Setting up ViewModel observers...")
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe loading state
            transactionViewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                Log.d(TAG, "Loading state: $isLoading")
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe transactions list
            transactionViewModel.transactions.collect { transactions ->
                Log.d(TAG, "Transactions updated: ${transactions.size} items")
                transactionAdapter.submitList(transactions)
                
                // Show/hide empty state
                binding.layoutEmptyState.visibility = 
                    if (transactions.isEmpty() && !transactionViewModel.isLoading.value) 
                        View.VISIBLE else View.GONE
                        
                binding.rvTransactions.visibility = 
                    if (transactions.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe error messages
            transactionViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e(TAG, "Error: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    transactionViewModel.clearErrorMessage()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Update filter button states based on current filters
            transactionViewModel.currentPaymentStatusFilter.collect { filter ->
                val activeFilter = when (filter) {
                    "pending" -> "pending"
                    "paid" -> "paid"
                    else -> "all"
                }
                updateFilterButtonStates(activeFilter)
            }
        }
        
        Log.d(TAG, "ViewModel observers setup completed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "TransactionsFragment destroyed")
        _binding = null
    }
}