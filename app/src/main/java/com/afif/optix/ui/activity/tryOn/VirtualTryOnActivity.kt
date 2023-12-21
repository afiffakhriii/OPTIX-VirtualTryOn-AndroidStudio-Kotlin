package com.afif.optix.ui.activity.tryOn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afif.optix.R

class VirtualTryOnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_try_on)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Tindakan yang diambil saat tombol panah kembali diklik
        onBackPressed()
        return true
    }
}
