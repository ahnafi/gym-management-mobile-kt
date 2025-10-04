package com.triosalak.gymmanagement.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.triosalak.gymmanagement.AuthActivity
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentRegisterBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        viewModel = AuthViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)

        // Observe register result
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            // Re-enable button after operation completes
            binding.btnRegister.isEnabled = true

            result.onSuccess { registerResponse ->
                Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                // Don't navigate immediately, let user see the success message
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Registrasi gagal: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Observe login result (for auto-login after registration)
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { loginResponse ->
//                Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()

                if (loginResponse.data.user.emailVerifiedAt == null) {
                    Toast.makeText(
                        requireContext(),
                        "Silakan verifikasi email Anda terlebih dahulu.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Navigate to VerifyEmailFragment
                    parentFragmentManager.beginTransaction()
                        .replace(
                            (activity as AuthActivity).findViewById<View>(com.triosalak.gymmanagement.R.id.auth_container).id,
                            VerifyEmailFragment()
                        )
                        .addToBackStack(null)
                        .commit()
                    return@onSuccess
                }

                // Navigate to main activity or dashboard
                (activity as? AuthActivity)?.navigateToMain()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Auto-login gagal: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                // Navigate to login fragment instead
                (activity as? AuthActivity)?.navigateToLogin()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val passwordConfirmation = binding.etRegisterPasswordConfirmation.text.toString().trim()
            val name = binding.etRegisterName.text.toString().trim()

            if (password != passwordConfirmation) {
                Toast.makeText(
                    requireContext(),
                    "Password dan konfirmasi password tidak sesuai",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple clicks
            binding.btnRegister.isEnabled = false

            viewModel.register(name, email, password, passwordConfirmation)
        }

        binding.btnGoToLogin.setOnClickListener {
            try {
                (activity as? AuthActivity)?.navigateToLogin()
            } catch (e: Exception) {
                Log.e("RegisterFragment", "Error navigating to login", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
