package com.triosalak.gymmanagement.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentChangePasswordBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        profileViewModel =
            ProfileViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)

        setupObservers()
        setupButtonClickListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            profileViewModel.isLoading.collect { isLoading ->
                binding.btnSavePassword.isEnabled = !isLoading
                if (isLoading) {
                    binding.btnSavePassword.text = "Menyimpan..."
                } else {
                    binding.btnSavePassword.text = "Save Password"
                }
            }
        }

        lifecycleScope.launch {
            profileViewModel.updateResult.collect { result ->
                result?.let {
                    when (it) {
                        is ProfileViewModel.UpdateResult.Success -> {
                            Toast.makeText(requireContext(), "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_changePasswordFragment_to_navigation_profile)
                        }
                        is ProfileViewModel.UpdateResult.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnSavePassword.setOnClickListener {
            if (validateInputs()) {
                val currentPassword = binding.tilCurrentPassword.editText?.text.toString().trim()
                val newPassword = binding.tilNewPassword.editText?.text.toString().trim()
                val newPasswordConfirmation = binding.tilNewPasswordConfirmation.editText?.text.toString().trim()

                profileViewModel.changePassword(currentPassword, newPassword, newPasswordConfirmation)
            }
        }

        // Handle the new back button in the layout
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_navigation_profile)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous errors
        binding.tilCurrentPassword.error = null
        binding.tilNewPassword.error = null
        binding.tilNewPasswordConfirmation.error = null

        val currentPassword = binding.tilCurrentPassword.editText?.text.toString().trim()
        val newPassword = binding.tilNewPassword.editText?.text.toString().trim()
        val newPasswordConfirmation = binding.tilNewPasswordConfirmation.editText?.text.toString().trim()

        // Validate current password
        if (currentPassword.isEmpty()) {
            binding.tilCurrentPassword.error = "Password saat ini harus diisi"
            isValid = false
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            binding.tilNewPassword.error = "Password baru harus diisi"
            isValid = false
        } else if (!isPasswordValid(newPassword)) {
            binding.tilNewPassword.error = "Password harus minimal 8 karakter, mengandung huruf besar, huruf kecil, dan angka"
            isValid = false
        }

        // Validate password confirmation
        if (newPasswordConfirmation.isEmpty()) {
            binding.tilNewPasswordConfirmation.error = "Konfirmasi password harus diisi"
            isValid = false
        } else if (newPassword != newPasswordConfirmation) {
            binding.tilNewPasswordConfirmation.error = "Password tidak cocok"
            isValid = false
        }

        // Check if new password is same as current password
        if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && currentPassword == newPassword) {
            binding.tilNewPassword.error = "Password baru harus berbeda dari password saat ini"
            isValid = false
        }

        return isValid
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) return false

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return hasUppercase && hasLowercase && hasDigit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}