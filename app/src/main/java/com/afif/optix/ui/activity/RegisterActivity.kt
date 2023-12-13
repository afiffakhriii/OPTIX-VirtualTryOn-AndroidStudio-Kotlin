package com.afif.optix.ui.activity

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afif.optix.R
import com.afif.optix.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginTextview.setOnClickListener { finish() }

        // Fading in the views
        fadeInViews()

        binding.btnRegister.setOnClickListener {
            if (isRegistrationDataValid()) {
                // Handle registration logic here (e.g., save to local database)
                // For simplicity, let's just show a toast message
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.registration_successful),
                    Toast.LENGTH_SHORT
                ).show()

                // You can also navigate to the login screen or perform other actions as needed
                finish()
            }
        }
    }


    private fun fadeInViews() {
        // Fading in each view with a slight delay for a smoother effect
        fadeInView(binding.registerImg, 150)
        fadeInView(binding.title, 300)
        fadeInView(binding.fieldName, 450)
        fadeInView(binding.fieldEmail, 600)
        fadeInView(binding.fieldPassword, 750)
        fadeInView(binding.fieldConfirmPassword, 900)
        fadeInView(binding.btnRegister, 1050)
        fadeInView(binding.continueWith, 1300)
        fadeInView(binding.divider2, 1300)
        fadeInView(binding.divider3, 1300)
        fadeInView(binding.googleSigninButton, 1450)
        fadeInView(binding.alreadyHaveAccount, 1600)
        fadeInView(binding.loginTextview, 1600)
    }

    private fun fadeInView(view: View, delay: Long) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setStartDelay(delay)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    private fun isRegistrationDataValid(): Boolean {
        val name = binding.edRegisterName.text?.toString()
        val email = binding.edRegisterEmail.text?.toString()
        val password = binding.edRegisterPassword.text?.toString()
        val confirmPassword = binding.edConfirmPassword.text?.toString()

        if (name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showToast(getString(R.string.fields_required))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.invalid_email_format))
            return false
        }

        if (password.length < 8) {
            showToast(getString(R.string.password_min_length))
            return false
        }

        if (password != confirmPassword) {
            showToast(getString(R.string.password_mismatch))
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}
