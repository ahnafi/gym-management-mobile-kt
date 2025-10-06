package com.triosalak.gymmanagement.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        loadStatistics()
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

            }.onFailure { exception ->
                // Handle error, e.g., show a Toast or log the error
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}