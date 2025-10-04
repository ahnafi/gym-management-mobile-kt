package com.triosalak.gymmanagement.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.triosalak.gymmanagement.AuthActivity
import com.triosalak.gymmanagement.MainActivity
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentLoginBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        viewModel = AuthViewModel(RetrofitInstance.getApiService(sessionManager), sessionManager)

        // Hide loading by default
        binding.loading.visibility = View.GONE

        // ✅ Observer login result
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            binding.loading.visibility = View.GONE
            result.fold(
                onSuccess = {
                    Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()

                    if (it.data.user.emailVerifiedAt == null) {
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
                        return@fold
                    } else {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }

                },
                onFailure = { error ->
                    Toast.makeText(
                        requireContext(),
                        "Login gagal: ${error.message ?: "Terjadi kesalahan"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        // ✅ Tombol Login
        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Email dan password harus diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi email sederhana
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Format email tidak valid", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            binding.loading.visibility = View.VISIBLE
            viewModel.login(email, password)
        }

        // ✅ Tombol ke Register - Fixed ID from go_to_register to goToRegister
        binding.goToRegister.setOnClickListener {
            try {
                (activity as? AuthActivity)?.navigateToRegister()
            } catch (e: Exception) {
                Log.e("LoginFragment", "Error navigating to register", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
