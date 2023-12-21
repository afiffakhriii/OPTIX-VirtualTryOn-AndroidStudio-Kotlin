package com.afif.optix.ui.activity.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.afif.optix.R
import com.afif.optix.data.utils.BitmapUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import de.hdodenhof.circleimageview.CircleImageView

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var profileCircleImageView: CircleImageView
    private lateinit var buttonPhoto: ImageButton
    private lateinit var penName: ImageButton
    private lateinit var penEmail: ImageButton

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.profile_activity_title)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")

        Log.d("ProfileSettingActivity", "Name: $name, Email: $email")

        profileCircleImageView = findViewById(R.id.iv_profile)
        val tvProfileName = findViewById<TextView>(R.id.tv_profile_name)
        val tvProfileEmail = findViewById<TextView>(R.id.tv_profile_email)
        profileCircleImageView = findViewById(R.id.iv_profile)
        buttonPhoto = findViewById(R.id.button_photo)
        penName = findViewById(R.id.pen_name)
        penEmail = findViewById(R.id.pen_email)

        tvProfileName.text = name
        tvProfileEmail.text = email

        buttonPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        penName.setOnClickListener {
            showEditDialog("Edit Name") { newName ->
                tvProfileName.text = newName
                // Update the name on Firebase and notify changes
                updateFirebaseUserName(newName)
            }
        }

        penEmail.setOnClickListener {
            showEditDialog("Edit Email") { newEmail ->
                tvProfileEmail.text = newEmail
                // Update the email on Firebase and notify changes
                updateFirebaseUserEmail(newEmail)
            }
        }
    }

    private fun updateNavDrawerProfileImage(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.def_pic)
            .into(profileCircleImageView)
    }

    private fun showImagePickerDialog() {
        val items = arrayOf("Camera", "Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
        builder.show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.type = "image/*"
        startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE)
    }

    private fun showEditDialog(title: String, onResult: (String) -> Unit) {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()
                onResult(newText)

                saveNameAndEmailToSharedPreferences(newText, title)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun saveNameAndEmailToSharedPreferences(value: String, title: String) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (title == "Edit Name") {
            editor.putString("name", value)
        } else if (title == "Edit Email") {
            editor.putString("email", value)
        }

        editor.apply()
    }

    private fun updateFirebaseUserName(newName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileSettingActivity", "User profile updated.")
                    // Notify changes to the calling activity (MainActivity)
                    notifyChanges()
                } else {
                    Log.e("ProfileSettingActivity", "Failed to update user profile.")
                }
            }
    }

    private fun updateFirebaseUserEmail(newEmail: String) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully updated user email address
                    Log.d("ProfileSettingActivity", "User email address updated.")

                    // Notify changes to the calling activity (MainActivity)
                    notifyChanges()

                    // Finish the ProfileSettingActivity
                    finish()
                } else {
                    // Failed to update user email address
                    Log.e("ProfileSettingActivity", "Failed to update user email address.")
                }
            }
    }

    private fun notifyChanges() {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    profileCircleImageView.setImageBitmap(imageBitmap)

                    // Convert the bitmap to URI and save it to SharedPreferences
                    val imageUri = BitmapUtils.getImageUri(this, imageBitmap)
                    saveImageUriToSharedPreferences(imageUri)
                }

                REQUEST_PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    profileCircleImageView.setImageURI(selectedImageUri)

                    saveImageUriToSharedPreferences(selectedImageUri)
                }
            }
        }
    }

    private fun saveImageUriToSharedPreferences(imageUri: Uri?) {
        if (imageUri != null) {
            val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("profileImageUri", imageUri.toString())
            editor.apply()

            updateNavDrawerProfileImage(imageUri)
        } else {
            Log.e("ProfileSettingActivity", "Image URI is null. Unable to save.")
        }
    }
}

