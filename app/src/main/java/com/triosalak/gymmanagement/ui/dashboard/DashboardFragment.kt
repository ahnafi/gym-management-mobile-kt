package com.triosalak.gymmanagement.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentDashboardBinding
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var recentVisitsAdapter: RecentVisitsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        dashboardViewModel = DashboardViewModel(RetrofitInstance.getApiService(sessionManager))

        setupRecentVisitsRecyclerView()
        loadStatistics()
    }

    private fun setupRecentVisitsRecyclerView() {
        recentVisitsAdapter = RecentVisitsAdapter()

        // Create RecyclerView programmatically
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentVisitsAdapter
            // Set max height to make it scrollable within the frame
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Clear the frame and add RecyclerView
        binding.frameRecentVisits.removeAllViews()
        binding.frameRecentVisits.addView(recyclerView)
    }

    private fun loadStatistics() {
        dashboardViewModel.getStatistic()

        val user = sessionManager.getCurrentUserSync()

        binding.textDashboard.text = "Selamat Datang, " + user?.name

        val imageUrl = "${Constants.STORAGE_URL}${user?.profileImage}"

        binding.ivProfilePicture.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_camera)
            error(R.drawable.ic_camera)
        }

        dashboardViewModel.myStatistic.observe(viewLifecycleOwner) { result ->
            result.onSuccess { statisticResponse ->
                val stats = statisticResponse.data
                binding.tvTotalVisits.text = stats.totalVisits.toString()
                binding.tvVisitsThisMonth.text = stats.visitsThisMonth.toString()
                binding.tvAverageVisitsPerWeek.text = stats.averageVisitsPerWeek.toString()
                binding.tvCurrentStreak.text = stats.currentStreak.toString()

                // Update recent visits adapter with data
                if (stats.recentVisits.isNotEmpty()) {
                    recentVisitsAdapter.updateData(stats.recentVisits)
                } else {
                    // Show empty state
                    showEmptyRecentVisits()
                }

            }.onFailure { exception ->
                // Handle error, e.g., show a Toast or log the error
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showEmptyRecentVisits() {
        binding.frameRecentVisits.removeAllViews()
        val emptyTextView = android.widget.TextView(requireContext()).apply {
            text = "Belum ada kunjungan gym"
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        binding.frameRecentVisits.addView(emptyTextView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}