package com.afif.optix.ui.activity.login

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afif.optix.MainActivity
import com.afif.optix.R
import com.afif.optix.databinding.ActivityLoginBinding
import com.afif.optix.ui.activity.register.RegisterActivity
import com.afif.optix.ui.activity.welcome.WelcomeActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var client: GoogleSignInClient
    private var isPasswordVisible = false

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    updateUI(null)
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = GoogleSignInOptions.Builder()
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this, options)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        fadeInAnimation(binding.layoutLogin, 300)
        fadeInAnimation(binding.loginImg, 600)
        fadeInAnimation(binding.title, 900)
        fadeInAnimation(binding.subTitle, 1200)
        fadeInAnimation(binding.fieldLoginName, 1500)
        fadeInAnimation(binding.edLoginName, 1500)
        fadeInAnimation(binding.fieldEmail, 1800)
        fadeInAnimation(binding.edLoginEmail, 1800)
        fadeInAnimation(binding.fieldPassword, 2100)
        fadeInAnimation(binding.edLoginPassword, 2100)
        fadeInAnimation(binding.btnLogin, 2400)
        fadeInAnimation(binding.continueWith, 2700)
        fadeInAnimation(binding.divider2, 2700)
        fadeInAnimation(binding.divider3, 2700)
        fadeInAnimation(binding.googleSigninButton, 3000)
        fadeInAnimation(binding.newToOptix, 3300)
        fadeInAnimation(binding.registerTextview, 3300)

        binding.registerTextview.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginImg = binding.loginImg

        Glide.with(this)
            .load("https://storage.googleapis.com/optixapp-staticassets/General/Login%20Model%20Picture.png")
            .into(loginImg)

        binding.btnLogin.setOnClickListener {
            val name = binding.edLoginName.text.toString()
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login success
                            val intent = Intent(this, WelcomeActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(
                                this,
                                getString(R.string.login_successful),
                                Toast.LENGTH_SHORT
                            ).show()

                            saveLoginStatus(name, email)

                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Show an error indicating all fields must be filled
                Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show()
            }

            // Check for a valid email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
            }

            // Check for minimum password length
            if (password.length < 8) {
                Toast.makeText(this, getString(R.string.password_min_length), Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleSigninButton.setOnClickListener {
            signInWithGoogle()
        }

        // Setup eye icon for password visibility toggle
        setupPasswordVisibilityToggle()
    }

    private fun setupPasswordVisibilityToggle() {
        val eyeDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
        binding.fieldPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.fieldPassword.setEndIconDrawable(eyeDrawable)

        binding.fieldPassword.setEndIconOnClickListener {
            // Toggle the password visibility
            isPasswordVisible = !isPasswordVisible

            // Call the function to toggle password visibility
            togglePasswordVisibility(isPasswordVisible, binding.fieldPassword, binding.edLoginPassword)
        }
    }

    private fun togglePasswordVisibility(
        isPasswordVisible: Boolean,
        fieldPassword: TextInputLayout,
        edLoginPassword: TextInputEditText
    ) {
        // Determine the new input type based on the visibility state
        val inputType = if (isPasswordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        edLoginPassword.inputType = inputType

        // Determine the new end drawable based on the visibility state
        val endDrawable: Drawable? = if (isPasswordVisible) {
            ContextCompat.getDrawable(this, R.drawable.baseline_visibility_24)
        } else {
            ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
        }
        fieldPassword.endIconDrawable = endDrawable
    }

    private fun fadeInAnimation(view: View, duration: Long) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = android.view.animation.AnticipateInterpolator()
        fadeIn.duration = duration

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}
        })

        view.startAnimation(fadeIn)
    }

    private fun signInWithGoogle() {
        val signInIntent = client.signInIntent
        launcher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    val user = firebaseAuth.currentUser
                    updateUI(user)

                    Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                } else {
                    updateUI(null)
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            saveLoginStatus(currentUser.displayName ?: "", currentUser.email ?: "")

            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                .putExtra("name", currentUser.displayName)
                .putExtra("email", currentUser.email)

            finish()

            startActivity(intent)
        }
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveLoginStatus(name: String, email: String) {
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("name", name)
        editor.putString("email", email)
        editor.apply()
    }
}

