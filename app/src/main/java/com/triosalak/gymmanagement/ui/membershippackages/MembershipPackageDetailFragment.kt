package com.triosalak.gymmanagement.ui.membershippackages

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.MembershipPackage
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentMembershipPackageDetailBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.MembershipPackageDetailViewModel
import com.triosalak.gymmanagement.viewmodel.MembershipPaymentViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MembershipPackageDetailFragment : Fragment() {

    private var _binding: FragmentMembershipPackageDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailViewModel: MembershipPackageDetailViewModel
    private lateinit var paymentViewModel: MembershipPaymentViewModel
    private lateinit var sessionManager: SessionManager
    private var currentPackage: MembershipPackage? = null

    private val packageId: Int by lazy {
        arguments?.getInt("packageId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipPackageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val apiService = RetrofitInstance.getApiService(sessionManager)
        detailViewModel = MembershipPackageDetailViewModel(apiService)
        paymentViewModel = MembershipPaymentViewModel(apiService)

        setupClickListeners()
        observeViewModel()
        loadPackageDetail()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelectPackage.setOnClickListener {
            currentPackage?.let { packageDetail ->
                showPaymentConfirmationDialog(packageDetail)
            } ?: run {
                Toast.makeText(requireContext(), "Data paket tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        detailViewModel.packageDetail.observe(viewLifecycleOwner) { result ->
            result.onSuccess { packageDetailResponse ->
                val packageDetail = packageDetailResponse.data
                currentPackage = packageDetail
                bindPackageData(packageDetail)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Payment ViewModel observers
        paymentViewModel.paymentResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { paymentResponse ->
                val snapToken = paymentResponse.data?.snapToken
                val message = "Pembayaran berhasil diinisiasi!\n\nSilakan lanjutkan pembayaran menggunakan Midtrans.\n\nToken: $snapToken"
                
                AlertDialog.Builder(requireContext())
                    .setTitle("Pembayaran Berhasil Diinisiasi")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ ->
                        // Here you can navigate to payment page or open Midtrans SDK
                        Toast.makeText(requireContext(), "Silakan selesaikan pembayaran Anda", Toast.LENGTH_LONG).show()
                    }
                    .setCancelable(false)
                    .show()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(), 
                    "Gagal memproses pembayaran: ${exception.message}", 
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        paymentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSelectPackage.isEnabled = !isLoading
            binding.btnSelectPackage.text = if (isLoading) "Memproses..." else "Pilih Paket"
        }
    }

    private fun loadPackageDetail() {
        detailViewModel.fetchPackageDetail(packageId)
    }

    private fun bindPackageData(packageDetail: MembershipPackage) {
        binding.apply {
            // Load package image
            ivPackageDetailImage.load(packageDetail.firstImageUrl) {
                crossfade(true)
                placeholder(R.drawable.exercise_24dp)
                error(R.drawable.exercise_24dp)
            }

            // Package details
            tvDetailCode.text = packageDetail.code ?: "PKG"
            tvDetailName.text = packageDetail.name ?: "Unknown Package"
            tvDetailSlug.text = packageDetail.slug ?: ""
            tvDetailStatus.text = packageDetail.status?.uppercase() ?: "ACTIVE"
            tvDetailDescription.text = packageDetail.description ?: "No description available"

            // Duration formatting
            val duration = packageDetail.duration ?: 0
            tvDetailDuration.text = when {
                duration == 0 -> "Sekali Bayar"
                duration >= 365 -> "${duration / 365} Tahun"
                duration >= 30 -> "${duration / 30} Bulan"
                else -> "$duration Hari"
            }

            // Format price
            val price = packageDetail.price ?: 0
            val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
            tvDetailPrice.text = formatter.format(price)

            // Format dates
            val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
            try {
                val createdDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
                    .parse(packageDetail.createdAt ?: "")
                val updatedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
                    .parse(packageDetail.updatedAt ?: "")

                tvDetailCreatedAt.text = createdDate?.let { dateFormat.format(it) } ?: "N/A"
                tvDetailUpdatedAt.text = updatedDate?.let { dateFormat.format(it) } ?: "N/A"
            } catch (e: Exception) {
                tvDetailCreatedAt.text = "N/A"
                tvDetailUpdatedAt.text = "N/A"
            }
        }
    }

    private fun showPaymentConfirmationDialog(packageDetail: MembershipPackage) {
        val price = packageDetail.price ?: 0
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        val formattedPrice = formatter.format(price)
        
        val message = """
            Apakah Anda yakin ingin membeli paket membership ini?
            
            Paket: ${packageDetail.name}
            Durasi: ${getDurationText(packageDetail.duration ?: 0)}
            Harga: Rp $formattedPrice
            
            Setelah konfirmasi, Anda akan diarahkan ke halaman pembayaran Midtrans.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Pembelian")
            .setMessage(message)
            .setPositiveButton("Ya, Beli Sekarang") { _, _ ->
                paymentViewModel.initiatePayment(packageDetail.id ?: 0)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun getDurationText(duration: Int): String {
        return when {
            duration == 0 -> "Sekali Bayar"
            duration >= 365 -> "${duration / 365} Tahun"
            duration >= 30 -> "${duration / 30} Bulan"
            else -> "$duration Hari"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}