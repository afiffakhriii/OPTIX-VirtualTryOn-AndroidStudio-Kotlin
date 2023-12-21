package com.afif.optix.ui.activity.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afif.optix.databinding.ActivityWelcomeBinding
import com.afif.optix.ui.activity.scan.ScanPictureActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWlcm.setOnClickListener {
            val intent = Intent(this, ScanPictureActivity::class.java)
            startActivity(intent)
        }
    }
}
