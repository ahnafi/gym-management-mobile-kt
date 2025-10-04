package com.triosalak.gymmanagement.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentVerifyEmailBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.AuthViewModel
import com.triosalak.gymmanagement.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class VerifyEmailFragment : Fragment() {

    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        authViewModel =
            AuthViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)
        profileViewModel =
            ProfileViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)

        setupObservers()
        setupButtonClickListeners()
    }

    private fun setupObservers() {
        // Observe currentUserResult untuk mengecek status verifikasi email
        lifecycleScope.launch {
            profileViewModel.currentUserResult.collect { result ->
                when (result) {
                    is ProfileViewModel.CurrentUserResult.Success -> {
                        val user = result.user
                        // Cek apakah emailVerifiedAt tidak null (sudah terverifikasi)
                        if (user.emailVerifiedAt != null) {
                            // Email sudah terverifikasi, navigasi ke MainActivity
                            Toast.makeText(
                                requireContext(),
                                "Email sudah terverifikasi! Menuju halaman utama...",
                                Toast.LENGTH_SHORT
                            ).show()
                            (activity as? com.triosalak.gymmanagement.AuthActivity)?.navigateToMain()
                        } else {
                            // Email belum terverifikasi
                            Toast.makeText(
                                requireContext(),
                                "Email belum terverifikasi. Silakan periksa email Anda.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    is ProfileViewModel.CurrentUserResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Email belum terverifikasi. Silakan periksa email Anda.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    null -> {
                        // Tidak ada result, tidak perlu action
                    }
                }
            }
        }
    }

    private fun setupButtonClickListeners() {

        binding.btnVerifyEmailResend.setOnClickListener {
            lifecycleScope.launch {
                // Implementasi pengiriman ulang email verifikasi
                authViewModel.resendVerificationEmail()
                Toast.makeText(
                    requireContext(),
                    "Email verifikasi telah dikirim ulang. Silakan periksa email Anda.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnVerifyEmailCheck.setOnClickListener {
            lifecycleScope.launch {
                profileViewModel.currentUser()

            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}