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
    private lateinit var membershipHistoryAdapter: MembershipHistoryAdapter

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
        setupMembershipHistoryRecyclerView()
        loadStatistics()
        loadMembershipHistory()
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

    private fun setupMembershipHistoryRecyclerView() {
        membershipHistoryAdapter = MembershipHistoryAdapter()

        // Find the membership history FrameLayout (it's the third FrameLayout in the layout)
        val membershipFrameLayout = binding.root.findViewById<android.widget.FrameLayout>(
            R.id.frame_membership_history
        ) ?: run {
            // If ID doesn't exist, find by traversing layout structure
            val scrollView =
                binding.root.findViewById<android.widget.ScrollView>(android.R.id.content)
            val linearLayout = scrollView?.getChildAt(0) as? android.widget.LinearLayout
            linearLayout?.getChildAt(linearLayout.childCount - 1) as? android.widget.LinearLayout
        }?.findViewById<android.widget.FrameLayout>(R.id.frame_membership_history)

        membershipFrameLayout?.let { frameLayout ->
            val recyclerView = RecyclerView(requireContext()).apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = membershipHistoryAdapter
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            frameLayout.removeAllViews()
            frameLayout.addView(recyclerView)
        }
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

        binding.tvMembershipEndDate.text = "Membership berakhir: ${user?.membershipEndDate}"

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

    private fun loadMembershipHistory() {
        dashboardViewModel.getMyMemberships()

        dashboardViewModel.myMemberships.observe(viewLifecycleOwner) { result ->
            result.onSuccess { membershipsResponse ->
                val memberships = membershipsResponse.data.memberships
                if (memberships.isNotEmpty()) {
                    membershipHistoryAdapter.updateData(memberships)
                } else {
                    showEmptyMembershipHistory()
                }
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error loading memberships: ${exception.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
                showEmptyMembershipHistory()
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

    private fun showEmptyMembershipHistory() {
        // Find the membership history FrameLayout
        val membershipFrameLayout = binding.root.findViewById<android.widget.FrameLayout>(
            R.id.frame_membership_history
        )

        membershipFrameLayout?.let { frameLayout ->
            frameLayout.removeAllViews()
            val emptyTextView = android.widget.TextView(requireContext()).apply {
                text = "Belum ada riwayat membership"
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            frameLayout.addView(emptyTextView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}