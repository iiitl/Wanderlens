package com.example.wanderlens.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlens.ui.auth.AuthActivity
import com.example.wanderlens.databinding.ActivitySplashBinding

import com.example.wanderlens.MainActivity
import com.example.wanderlens.repository.AuthRepository

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate().alpha(1f).setDuration(1500).start()

        binding.tvLogo.alpha = 0f
        binding.tvLogo.animate().alpha(1f).setDuration(1500).start()

        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate().alpha(1f).setDuration(1500).setStartDelay(300).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (authRepository.isUserLoggedIn()) {
                MainActivity::class.java
            } else {
                AuthActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
        }, 2500)
    }
}