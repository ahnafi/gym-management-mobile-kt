package com.triosalak.gymmanagement

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.triosalak.gymmanagement.databinding.ActivityAuthBinding
import com.triosalak.gymmanagement.ui.auth.LoginFragment
import com.triosalak.gymmanagement.ui.auth.RegisterFragment
import com.triosalak.gymmanagement.ui.auth.VerifyEmailFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we should show email verification directly
        val showEmailVerification = intent.getBooleanExtra("show_email_verification", false)

        // Default ke LoginFragment atau VerifyEmailFragment berdasarkan intent
        if (savedInstanceState == null) {
            if (showEmailVerification) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.authContainer.id, VerifyEmailFragment())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(binding.authContainer.id, LoginFragment())
                    .commit()
            }
        }
    }

    fun navigateToEmailVerification(){
        supportFragmentManager.beginTransaction()
            .replace(binding.authContainer.id, VerifyEmailFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(binding.authContainer.id, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(binding.authContainer.id, LoginFragment())
            .commit()
    }

    fun navigateToMain() {
        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
