package com.triosalak.gymmanagement.ui.membershippackages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private lateinit var membershipPackageAdapter: MembershipPackageAdapter

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

        sessionManager = SessionManager(requireContext())
        membershipPackagesViewModel = MembershipPackagesViewModel(RetrofitInstance.getApiService(sessionManager))

        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerView() {
        membershipPackageAdapter = MembershipPackageAdapter { membershipPackage ->
            onPackageSelected(membershipPackage)
        }

        binding.rvMembershipPackages.apply {
            adapter = membershipPackageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        membershipPackagesViewModel.membershipPackages.observe(viewLifecycleOwner) { result ->
            result.onSuccess { membershipPackagesResponse ->
                val packages = membershipPackagesResponse.data.data // Access nested data
                membershipPackageAdapter.submitList(packages)
                
                if (packages.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                }
            }.onFailure { exception ->
                showEmptyState(true)
                // Show more detailed error message
                val errorMessage = exception.message ?: "Unknown error occurred"
                Toast.makeText(requireContext(), "Error loading packages: $errorMessage", Toast.LENGTH_LONG).show()
                
                // Log the error for debugging
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
        // Option 1: Using Navigation Component
        try {
            val packageId = membershipPackage.id ?: return
            val bundle = Bundle().apply {
                putInt("packageId", packageId)
            }
            findNavController().navigate(R.id.membershipPackageDetailFragment, bundle)
        } catch (e: Exception) {
            // Option 2: Fallback to Fragment Transaction
            navigateToDetailWithFragmentTransaction(membershipPackage)
        }
    }
    
    private fun navigateToDetailWithFragmentTransaction(membershipPackage: MembershipPackage) {
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
        binding.layoutEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvMembershipPackages.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}