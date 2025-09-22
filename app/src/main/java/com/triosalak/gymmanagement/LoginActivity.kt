package com.triosalak.gymmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.triosalak.gymmanagement.databinding.ActivityLoginBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.LoginViewModel
import com.triosalak.gymmanagement.data.netwok.RetrofitInstance

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = LoginViewModel(RetrofitInstance.api, sessionManager)

        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { loginResponse ->
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    // Navigate to main activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, "Login gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }
    }
}
