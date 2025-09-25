package com.triosalak.gymmanagement.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.triosalak.gymmanagement.databinding.FragmentProfileBinding
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // Setup button click listeners
        setupButtonClickListeners()

        // Cara 1: Menggunakan Flow (Realtime Updates)
        observeUserData()

        // Cara 2: Menggunakan Method Synchronous (One-time)
        loadUserDataSync()
    }

    private fun setupButtonClickListeners() {
        // Logout button
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        // Edit profile button (placeholder for future implementation)
        binding.btnEditProfile.setOnClickListener {
            // TODO: Navigate to edit profile screen
            android.widget.Toast.makeText(requireContext(), "Edit Profile coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                // Clear all user data
                sessionManager.clearAllData()

                // Navigate back to AuthActivity
                val intent = android.content.Intent(requireContext(), com.triosalak.gymmanagement.AuthActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                android.util.Log.e("ProfileFragment", "Error during logout", e)
                android.widget.Toast.makeText(requireContext(), "Error saat logout: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeUserData() {
        // Observe user data secara realtime
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    // Update UI dengan data user
                    binding.apply {
                        tvUserName.text = it.name ?: "Nama tidak tersedia"
                        tvUserEmail.text = it.email
                        tvUserPhone.text = it.phone ?: "Phone tidak tersedia"
                        tvUserRole.text = it.role ?: "Role tidak tersedia"
                        tvMembershipStatus.text = it.membershipStatus ?: "Tidak aktif"
                        tvMembershipEndDate.text = it.membershipEndDate ?: "Tidak ada"
                        tvProfileBio.text = it.profileBio ?: "Bio kosong"

                        // Log semua data user
                        android.util.Log.d("USER_DATA", "ID: ${it.id}")
                        android.util.Log.d("USER_DATA", "Name: ${it.name}")
                        android.util.Log.d("USER_DATA", "Email: ${it.email}")
                        android.util.Log.d("USER_DATA", "Phone: ${it.phone}")
                        android.util.Log.d("USER_DATA", "Role: ${it.role}")
                        android.util.Log.d("USER_DATA", "Membership: ${it.membershipStatus}")
                        android.util.Log.d("USER_DATA", "Created: ${it.createdAt}")
                    }
                }
            }
        }
    }

    private fun loadUserDataSync() {
        // Cara mengambil user data secara synchronous
        val currentUser = sessionManager.getCurrentUserSync()
        currentUser?.let { user ->
            android.util.Log.d("USER_SYNC", "User loaded: ${user.name} - ${user.email}")
        }
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val isLoggedIn = sessionManager.isUserLoggedIn()
            if (isLoggedIn) {
                android.util.Log.d("LOGIN_STATUS", "User is logged in")
            } else {
                android.util.Log.d("LOGIN_STATUS", "User is not logged in")
                // Redirect to login
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
