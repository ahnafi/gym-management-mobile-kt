package com.triosalak.gymmanagement.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.triosalak.gymmanagement.AuthActivity
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentVerifyEmailBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class VerifyEmailFragment : Fragment() {

    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private var isCheckingStatus = false // Flag to prevent infinite loop

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        authViewModel = AuthViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)

        setupUI()
        setupObservers()
        displayUserEmail()
    }

    private fun setupUI() {
        binding.btnResendVerification.setOnClickListener {
            resendVerificationEmail()
        }

        binding.btnCheckStatus.setOnClickListener {
            checkVerificationStatus()
        }

        binding.tvBackToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun setupObservers() {
        // Observer untuk hasil resend verification
        authViewModel.resendVerificationResult.observe(viewLifecycleOwner) { result ->
            hideLoading()
            result.onSuccess { response ->
                showStatusMessage("Email verifikasi berhasil dikirim ulang. Silakan cek email Anda.", true)
            }.onFailure { error ->
                showStatusMessage("Gagal mengirim ulang email: ${error.message}", false)
            }
        }

        // Observer untuk status verifikasi email
        authViewModel.emailVerificationStatus.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified) {
                showStatusMessage("Email Anda sudah diverifikasi! Mengarahkan ke dashboard...", true)
                // Delay sebentar lalu navigate ke MainActivity
                binding.root.postDelayed({
                    navigateToMain()
                }, 2000)
            }
        }

        // Observer untuk hasil getCurrentUser
        authViewModel.currentUserResult.observe(viewLifecycleOwner) { result ->
            hideLoading()
            result.onSuccess { currentUserResponse ->
                val user = currentUserResponse.data
                // Update user email display
                binding.tvUserEmail.text = user.email

                // Check verification status
                if (user.emailVerifiedAt != null) {
                    showStatusMessage("Email Anda sudah diverifikasi! Mengarahkan ke dashboard...", true)
                    binding.root.postDelayed({
                        navigateToMain()
                    }, 2000)
                } else {
                    showStatusMessage("Email belum diverifikasi. Silakan cek email Anda dan klik link verifikasi.", false)
                }
            }.onFailure { error ->
                showStatusMessage("Gagal mengecek status: ${error.message}", false)
            }
        }
    }

    private fun displayUserEmail() {
        lifecycleScope.launch {
            val currentUser = sessionManager.getCurrentUserSync()
            currentUser?.let { user ->
                binding.tvUserEmail.text = user.email
            }
        }
    }

    private fun resendVerificationEmail() {
        showLoading("Mengirim ulang email verifikasi...")
        authViewModel.resendVerification()
    }

    private fun checkVerificationStatus() {
        if (isCheckingStatus) return // Prevent multiple clicks
        isCheckingStatus = true

        showLoading("Mengecek status verifikasi...")
        // Gunakan API untuk mendapatkan data terbaru dari server
        authViewModel.getCurrentUser()
    }

    private fun showLoading(message: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatusMessage.visibility = View.VISIBLE
        binding.tvStatusMessage.text = message
        binding.tvStatusMessage.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        binding.btnResendVerification.isEnabled = false
        binding.btnCheckStatus.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnResendVerification.isEnabled = true
        binding.btnCheckStatus.isEnabled = true
        isCheckingStatus = false
    }

    private fun showStatusMessage(message: String, isSuccess: Boolean) {
        binding.tvStatusMessage.visibility = View.VISIBLE
        binding.tvStatusMessage.text = message

        val color = if (isSuccess) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }
        binding.tvStatusMessage.setTextColor(resources.getColor(color, null))

        // Auto hide message after 5 seconds
        binding.root.postDelayed({
            if (binding.tvStatusMessage.text == message) {
                binding.tvStatusMessage.visibility = View.GONE
            }
        }, 5000)
    }

    private fun navigateToLogin() {
        (requireActivity() as? AuthActivity)?.navigateToLogin()
    }

    private fun navigateToMain() {
        (requireActivity() as? AuthActivity)?.navigateToMain()
    }

    override fun onResume() {
        super.onResume()
        // Cek status verifikasi setiap kali fragment di-resume
        // (misalnya user kembali dari email app)
        checkVerificationStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}