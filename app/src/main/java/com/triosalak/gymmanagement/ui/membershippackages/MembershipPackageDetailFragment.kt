package com.triosalak.gymmanagement.ui.membershippackages

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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MembershipPackageDetailFragment : Fragment() {

    private var _binding: FragmentMembershipPackageDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailViewModel: MembershipPackageDetailViewModel
    private lateinit var sessionManager: SessionManager

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
        detailViewModel = MembershipPackageDetailViewModel(RetrofitInstance.getApiService(sessionManager))

        setupClickListeners()
        observeViewModel()
        loadPackageDetail()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelectPackage.setOnClickListener {
            // Handle package selection
            Toast.makeText(requireContext(), "Package selected!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSharePackage.setOnClickListener {
            // Handle package sharing
            Toast.makeText(requireContext(), "Share functionality coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        detailViewModel.packageDetail.observe(viewLifecycleOwner) { result ->
            result.onSuccess { packageDetailResponse ->
                val packageDetail = packageDetailResponse.data
                bindPackageData(packageDetail)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}