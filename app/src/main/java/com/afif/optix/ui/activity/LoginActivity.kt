package com.afif.optix.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afif.optix.MainActivity
import com.afif.optix.R
import com.afif.optix.databinding.ActivityLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        binding.registerTextview.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (isLoginDataValid()) {
                binding.loading.visibility = View.VISIBLE
                // Simulate the login process without actual API calls
                simulateLogin()
            }
        }
        fadeInViews()
    }

    private fun simulateLogin() {
        // Simulate a successful login
        lifecycleScope.launch {
            // In a real app, you would make an API call here
            // For the sake of simplicity, we'll just simulate a delay
            delay(2000)
            // Assuming login is successful
            onLoginSuccess()
        }
    }

    private fun onLoginSuccess() {
        sharedPreferences
            .edit()
            .putString("token", "your_token_here") // Replace with the actual token
            .apply()

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun isLoginDataValid(): Boolean {
        val email = binding.edLoginEmail.text?.toString()
        val password = binding.edLoginPassword.text?.toString()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showToast(getString(R.string.fields_required))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.invalid_email_format))
            return false
        }
        if (password.length < 8) {
            Toast.makeText(this, getString(R.string.password_min_length), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }


    private fun fadeInViews() {
        // Fading in each view with a slight delay for a smoother effect
        fadeInView(binding.loginImg, 150)
        fadeInView(binding.title, 300)
        fadeInView(binding.subTitle, 600)
        fadeInView(binding.fieldEmail, 750)
        fadeInView(binding.fieldPassword, 900)
        fadeInView(binding.btnLogin, 1050)
        fadeInView(binding.continueWith, 1300)
        fadeInView(binding.divider2, 1300)
        fadeInView(binding.divider3, 1300)
        fadeInView(binding.googleSigninButton, 1450)
        fadeInView(binding.newToOptix, 1600)
        fadeInView(binding.registerTextview, 1600)
    }

    private fun fadeInView(view: View, delay: Long) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setStartDelay(delay)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }
}