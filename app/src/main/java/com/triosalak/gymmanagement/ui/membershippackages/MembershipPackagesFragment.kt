package com.triosalak.gymmanagement.ui.membershippackages

import HeaderAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter // <-- Perubahan: Import ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.MembershipPackage
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentMembershipPackagesBinding
import com.triosalak.gymmanagement.ui.membershippackages.adapter.MembershipPackageAdapter
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.MembershipPackagesViewModel

class MembershipPackagesFragment : Fragment() {

    private var _binding: FragmentMembershipPackagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var membershipPackagesViewModel: MembershipPackagesViewModel
    private lateinit var sessionManager: SessionManager

    // --- PERUBAHAN: Deklarasi untuk 3 adapter ---
    private lateinit var membershipPackageAdapter: MembershipPackageAdapter
    private lateinit var headerAdapter: HeaderAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipPackagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel tetap sama
        sessionManager = SessionManager(requireContext())
        membershipPackagesViewModel = MembershipPackagesViewModel(RetrofitInstance.getApiService(sessionManager))

        // Panggil fungsi-fungsi utama
        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerView() {
        // 1. Inisialisasi kedua adapter secara terpisah
        headerAdapter = HeaderAdapter()
        membershipPackageAdapter = MembershipPackageAdapter { membershipPackage ->
            onPackageSelected(membershipPackage)
        }

        // 2. Gabungkan keduanya menggunakan ConcatAdapter
        concatAdapter = ConcatAdapter(headerAdapter, membershipPackageAdapter)

        // 3. Atur RecyclerView untuk menggunakan ConcatAdapter
        binding.rvMembershipPackages.apply {
            adapter = concatAdapter // Menggunakan adapter gabungan
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        membershipPackagesViewModel.membershipPackages.observe(viewLifecycleOwner) { result ->
            result.onSuccess { membershipPackagesResponse ->
                val packages = membershipPackagesResponse.data.data

                // Update hanya adapter paket, header tidak perlu diubah
                membershipPackageAdapter.submitList(packages)

                // Logika untuk empty state sedikit berubah
                if (packages.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                }
            }.onFailure { exception ->
                showEmptyState(true)
                val errorMessage = exception.message ?: "Unknown error occurred"
                Toast.makeText(requireContext(), "Error loading packages: $errorMessage", Toast.LENGTH_LONG).show()
                android.util.Log.e("MembershipPackages", "Failed to load packages", exception)
            }
        }

        membershipPackagesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadData() {
        membershipPackagesViewModel.fetchMembershipPackages()
    }

    private fun onPackageSelected(membershipPackage: MembershipPackage) {
        // Logika navigasi tidak perlu diubah
        try {
            val packageId = membershipPackage.id ?: return
            val bundle = Bundle().apply {
                putInt("packageId", packageId)
            }
            findNavController().navigate(R.id.membershipPackageDetailFragment, bundle)
        } catch (e: Exception) {
            navigateToDetailWithFragmentTransaction(membershipPackage)
        }
    }

    private fun navigateToDetailWithFragmentTransaction(membershipPackage: MembershipPackage) {
        // Logika navigasi tidak perlu diubah
        val packageId = membershipPackage.id ?: return
        val detailFragment = MembershipPackageDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("packageId", packageId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showEmptyState(show: Boolean) {
        // PERUBAHAN: Saat empty state, RecyclerView tetap terlihat agar header-nya muncul
        binding.layoutEmptyState.visibility = if (show) View.VISIBLE else View.GONE

        // Kosongkan data di adapter jika empty state ditampilkan
        if (show) {
            membershipPackageAdapter.submitList(emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
