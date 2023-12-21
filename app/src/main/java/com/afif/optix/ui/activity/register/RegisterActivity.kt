package com.afif.optix.ui.activity.register

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afif.optix.R
import com.afif.optix.databinding.ActivityRegisterBinding
import com.afif.optix.ui.activity.login.LoginActivity
import com.afif.optix.ui.activity.welcome.WelcomeActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        fadeInAnimation(binding.registerImg, 300)
        fadeInAnimation(binding.title, 600)
        fadeInAnimation(binding.fieldName, 900)
        fadeInAnimation(binding.edRegisterName, 900)
        fadeInAnimation(binding.fieldEmail, 1200)
        fadeInAnimation(binding.edRegisterEmail, 1200)
        fadeInAnimation(binding.fieldPassword, 1500)
        fadeInAnimation(binding.edRegisterPassword, 1500)
        fadeInAnimation(binding.fieldConfirmPassword, 1800)
        fadeInAnimation(binding.edConfirmPassword, 1800)
        fadeInAnimation(binding.btnRegister, 2100)
        fadeInAnimation(binding.continueWith, 2400)
        fadeInAnimation(binding.divider3, 2400)
        fadeInAnimation(binding.divider4, 2400)
        fadeInAnimation(binding.googleSigninButtonRegis, 2700)
        fadeInAnimation(binding.alreadyHaveAccount, 3000)
        fadeInAnimation(binding.loginTextview, 3000)

        Glide.with(this)
            .load("https://storage.googleapis.com/optixapp-staticassets/General/Registration%20Model%20Picture.png")
            .into(binding.registerImg)

        setupPasswordVisibilityToggle()

        setupConfirmPasswordVisibilityToggle()

        configureGoogleSignIn()

        binding.googleSigninButtonRegis.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (password.length >= 8) {
                        if (password == confirmPassword) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            getString(R.string.registration_successful),
                                            Toast.LENGTH_SHORT
                                        ).show()


                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // Registration failed
                                        Toast.makeText(
                                            this,
                                            "Registration failed: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.password_mismatch),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.password_min_length),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.invalid_email_format),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginTextview.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fadeInAnimation(view: View, duration: Long) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = android.view.animation.AnticipateInterpolator()
        fadeIn.duration = duration

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        view.startAnimation(fadeIn)
    }

    private fun setupPasswordVisibilityToggle() {
        val eyeDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
        binding.fieldPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.fieldPassword.setEndIconDrawable(eyeDrawable)

        binding.fieldPassword.setEndIconOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Determine the new end drawable based on the visibility state
            val endDrawable: Drawable? = if (isPasswordVisible) {
                ContextCompat.getDrawable(this, R.drawable.baseline_visibility_24)
            } else {
                ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
            }

            binding.fieldPassword.endIconDrawable = endDrawable

            val inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edRegisterPassword.inputType = inputType
        }
    }

    private fun setupConfirmPasswordVisibilityToggle() {
        val eyeDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
        binding.fieldConfirmPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.fieldConfirmPassword.setEndIconDrawable(eyeDrawable)

        binding.fieldConfirmPassword.setEndIconOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Determine the new end drawable based on the visibility state
            val endDrawable: Drawable? = if (isPasswordVisible) {
                ContextCompat.getDrawable(this, R.drawable.baseline_visibility_24)
            } else {
                ContextCompat.getDrawable(this, R.drawable.baseline_visibility_off_24)
            }

            binding.fieldConfirmPassword.endIconDrawable = endDrawable

            // Toggle the input type between password and text
            val inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edConfirmPassword.inputType = inputType
        }
    }


    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()

        } catch (e: ApiException) {
            Toast.makeText(
                this,
                "Google Sign-In failed: ${e.statusCode}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
