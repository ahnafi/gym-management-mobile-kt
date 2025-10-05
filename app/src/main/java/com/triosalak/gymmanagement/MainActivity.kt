package com.triosalak.gymmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.triosalak.gymmanagement.databinding.ActivityMainBinding
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            sessionManager = SessionManager(this)

            lifecycleScope.launch {
                try {
                    val token = sessionManager.authToken.firstOrNull()
                    Log.d("CekToken", "Token retrieved: $token")

                    if (token.isNullOrBlank()) {
                        // Kalau token kosong, paksa ke AuthActivity
                        navigateTo(AuthActivity::class.java)
                    } else {
                        // Kalau token ada, langsung setup tampilan utama
                        setupBottomNav()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error checking token: ${e.message}")
                    // Jika ada error, langsung ke AuthActivity
                    navigateTo(AuthActivity::class.java)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}")
            // Jika binding atau setup gagal, langsung ke AuthActivity
            navigateTo(AuthActivity::class.java)
        }
    }

    private fun setupBottomNav() {
        try {
            val navView: BottomNavigationView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            // Sembunyikan action bar
            supportActionBar?.hide()

            // Setup bottom navigation dengan nav controller
            navView.setupWithNavController(navController)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting up navigation: ${e.message}")
            // Jika navigation setup gagal, kembali ke AuthActivity
            navigateTo(AuthActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        try {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error navigating: ${e.message}")
        }
    }
}
