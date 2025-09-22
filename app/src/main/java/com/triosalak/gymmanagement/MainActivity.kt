package com.triosalak.gymmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.triosalak.gymmanagement.databinding.ActivityMainBinding
import com.triosalak.gymmanagement.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

//        val token = sessionManager.authToken.first()
//
//        Log.d("CekToken", "Token retrieved: $token")
//
//        // Cek apakah token ada dan tidak kosong
//        if (!token.isNullOrBlank()) {
//            // Token ada, lanjutkan ke MainActivity
//            navigateTo(MainActivity::class.java)
//        } else {
//            navigateTo(LoginActivity::class.java)
//        }

        lifecycleScope.launch {

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_dashboard,
                    R.id.navigation_notifications
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)


            val token = sessionManager.authToken.first()

            Log.d("CekToken", "Token retrieved: $token")

            // Cek apakah token ada dan tidak kosong
            if (!token.isNullOrBlank()) {
                // Token ada, lanjutkan ke MainActivity
                navigateTo(MainActivity::class.java)
            } else {
                navigateTo(LoginActivity::class.java)
            }

        }

    }

    /**
     * Fungsi helper untuk navigasi dan menutup activity saat ini.
     */
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }
}