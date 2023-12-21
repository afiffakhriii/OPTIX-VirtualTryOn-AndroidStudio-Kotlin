// MainActivity.kt

package com.afif.optix

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afif.optix.databinding.ActivityMainBinding
import com.afif.optix.ui.activity.login.LoginActivity
import com.afif.optix.ui.activity.setting.ProfileSettingActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var profileCircleImageView: CircleImageView
    private lateinit var profileNameTextView: TextView
    private lateinit var profileEmailTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    private val PROFILE_SETTING_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        if (!isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView = binding.navView
        profileCircleImageView = navView.getHeaderView(0).findViewById(R.id.iv_profile)

        profileNameTextView = navView.getHeaderView(0).findViewById(R.id.profileName)
        profileEmailTextView = navView.getHeaderView(0).findViewById(R.id.profileEmail)


        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val imageUriString = sharedPreferences.getString("profileImageUri", null)
        val imageUri = imageUriString?.let { Uri.parse(it) }
        updateNavDrawerProfileImage(imageUri)

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null && currentUser.email != null) {
            profileEmailTextView.text = currentUser.email
        }

        val savedName = sharedPreferences.getString("name", "")
        if (!savedName.isNullOrEmpty()) {
            profileNameTextView.text = savedName
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_wishlist, R.id.nav_cart),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }

                R.id.nav_profile_setting -> {
                    openProfileSettingActivity()
                    true
                }

                else -> {
                    menuItem.onNavDestinationSelected(navController)
                    drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer here
                    true
                }
            }
        }
    }

    private fun openProfileSettingActivity() {
        val name = profileNameTextView.text.toString()
        val email = profileEmailTextView.text.toString()

        val profileIntent = Intent(this, ProfileSettingActivity::class.java)
        profileIntent.putExtra("name", name)
        profileIntent.putExtra("email", email)
        startActivityForResult(profileIntent, PROFILE_SETTING_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_SETTING_REQUEST && resultCode == Activity.RESULT_OK) {
            val updatedName = data?.getStringExtra("updatedName")
            val updatedEmail = data?.getStringExtra("updatedEmail")

            if (!updatedName.isNullOrEmpty()) {
                profileNameTextView.text = updatedName
            }

            if (!updatedEmail.isNullOrEmpty()) {
                profileEmailTextView.text = updatedEmail
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_logout -> {
                performLogout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val drawerLayout: DrawerLayout = binding.drawerLayout

        return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        } else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }


    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_logout_builder)
            .setMessage(R.string.text_logout_dialog)
            .setPositiveButton(R.string.yes_txt) { _, _ ->
                // User clicked Yes
                performLogout()
            }
            .setNegativeButton(R.string.cancel_txt) { dialog, _ ->
                // User clicked Cancel
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            remove("token")
            putBoolean("isLoggedIn", false)
            // Hapus perubahan nama dan foto profil
            remove("name")
            remove("profileImageUri")
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun updateNavDrawerProfileImage(imageUri: Uri?) {
        if (imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.def_pic) // Placeholder image if the URI is null
                .into(profileCircleImageView)
        } else {
            Log.e("MainActivity", "Image URI is null. Unable to update profile image.")
        }
    }
}
