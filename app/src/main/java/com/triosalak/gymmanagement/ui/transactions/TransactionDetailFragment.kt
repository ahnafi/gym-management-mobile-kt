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
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.PurchasableGymClass
import com.triosalak.gymmanagement.data.model.entity.PurchasableMembershipPackage
import com.triosalak.gymmanagement.data.model.entity.Transaction
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentTransactionDetailBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.TransactionDetailViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionDetailViewModel: TransactionDetailViewModel
    private lateinit var sessionManager: SessionManager
    
    // Get transaction ID from navigation arguments
    private val transactionId: Int by lazy {
        arguments?.getInt("transactionId") ?: 0
    }

    companion object {
        private const val TAG = "TransactionDetailFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "=== TransactionDetailFragment onViewCreated ===")

        setupDependencies()
        setupBackButton()
        observeViewModel()
        loadTransactionDetail()
        
        Log.i(TAG, "✅ TransactionDetailFragment setup completed")
    }

    private fun setupDependencies() {
        Log.d(TAG, "Setting up dependencies...")
        sessionManager = SessionManager(requireContext())
        
        val api = RetrofitInstance.getApiService(sessionManager)
        transactionDetailViewModel = TransactionDetailViewModel(api)
        Log.d(TAG, "Dependencies initialized successfully")
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            findNavController().navigateUp()
        }
    }

    private fun loadTransactionDetail() {
        Log.d(TAG, "Loading transaction detail for ID: $transactionId")
        
        if (transactionId == 0) {
            Log.e(TAG, "Invalid transaction ID: $transactionId")
            Toast.makeText(requireContext(), "ID transaksi tidak valid", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Load transaction detail using dedicated ViewModel
        transactionDetailViewModel.loadTransactionDetail(transactionId)
    }

    private fun observeViewModel() {
        Log.d(TAG, "Setting up ViewModel observers...")
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe loading state
            transactionDetailViewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
                Log.d(TAG, "Loading state: $isLoading")
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe transaction detail
            transactionDetailViewModel.transaction.collect { transaction ->
                transaction?.let {
                    Log.d(TAG, "Transaction loaded: ${it.code}")
                    displayTransactionDetails(it)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe error messages
            transactionDetailViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Log.e(TAG, "Error: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    transactionDetailViewModel.clearErrorMessage()
                }
            }
        }
        
        Log.d(TAG, "ViewModel observers setup completed")
    }

    private fun displayTransactionDetails(transaction: Transaction) {
        Log.d(TAG, "Displaying transaction details: ${transaction.code}")
        
        with(binding) {
            // Transaction code and status
            tvDetailTransactionCode.text = transaction.code ?: "N/A"
            setPaymentStatus(transaction.paymentStatus)

            // Transaction info
            setPurchasableTypeAndName(transaction)
            setAmount(transaction.amount)
            setDates(transaction.createdAt, transaction.paymentDate)
        }
    }

    private fun setPaymentStatus(status: String?) {
        val (statusText, statusColor) = when (status?.lowercase()) {
            "pending" -> Pair("Menunggu Pembayaran", R.color.myred)
            "paid" -> Pair("Pembayaran Berhasil", android.R.color.holo_green_dark)
            "failed" -> Pair("Pembayaran Gagal", android.R.color.holo_red_dark)
            else -> Pair("Status Tidak Diketahui", android.R.color.darker_gray)
        }

        binding.tvDetailPaymentStatus.text = statusText
        binding.tvDetailPaymentStatus.setTextColor(
            ContextCompat.getColor(requireContext(), statusColor)
        )
    }

    private fun setPurchasableTypeAndName(transaction: Transaction) {
        val purchasable = transaction.purchasable
        val purchasableType = transaction.purchasableType

        // Set purchasable type with localization
        val typeText = when (purchasableType) {
            "membership_package" -> "Paket Membership"
            "gym_class" -> "Kelas Gym"
            else -> purchasableType ?: "Unknown"
        }
        binding.tvDetailPurchasableType.text = typeText

        // Set item name based on purchasable type
        val itemName = when (purchasable) {
            is PurchasableMembershipPackage -> purchasable.name ?: "Paket Membership"
            is PurchasableGymClass -> purchasable.name ?: "Kelas Gym"
            else -> "Item tidak diketahui"
        }
        binding.tvDetailItemName.text = itemName
    }

    private fun setAmount(amount: Int?) {
        if (amount != null) {
            val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
            binding.tvDetailAmount.text = formatter.format(amount)
        } else {
            binding.tvDetailAmount.text = "0"
        }
    }

    private fun setDates(createdAt: String?, paymentDate: String?) {
        // Created date
        if (createdAt != null) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                
                val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
                
                val date = inputFormat.parse(createdAt)
                binding.tvDetailCreatedAt.text = date?.let { outputFormat.format(it) } ?: createdAt
            } catch (e: Exception) {
                binding.tvDetailCreatedAt.text = createdAt
            }
        } else {
            binding.tvDetailCreatedAt.text = "N/A"
        }

        // Payment date (show only if transaction is paid)
        if (paymentDate != null) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                
                val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
                
                val date = inputFormat.parse(paymentDate)
                binding.tvDetailPaymentDate.text = date?.let { outputFormat.format(it) } ?: paymentDate
                binding.layoutPaymentDate.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.tvDetailPaymentDate.text = paymentDate
                binding.layoutPaymentDate.visibility = View.VISIBLE
            }
        } else {
            binding.layoutPaymentDate.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "TransactionDetailFragment destroyed")
        _binding = null
    }
}