package com.afif.optix.ui.activity.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.afif.optix.R
import com.afif.optix.data.entity.ProductEntity
import com.afif.optix.ui.viewModel.SharedViewModel
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var product: ProductEntity
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inisialisasi ViewModel
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Mendapatkan data yang dikirimkan dari Intent
        val productImage = intent.getStringExtra("PRODUCT_IMAGE")
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productPrice = intent.getLongExtra("PRODUCT_PRICE", 0L)

        // Inisialisasi objek ProductEntity
        product = ProductEntity(productImage, productName, productPrice)

        // Menemukan elemen UI di layout
        val imageViewProduct: ImageView = findViewById(R.id.imageView_product)
        val tvProductName: TextView = findViewById(R.id.tv_detail_product_name)
        val tvProductPrice: TextView = findViewById(R.id.tv_detail_price)
        val imageButtonFavorite: ImageButton = findViewById(R.id.imageButton_favorite)

        // Menampilkan data pada elemen UI
        Glide.with(this)
            .load(product.imageUrl)
            .into(imageViewProduct)

        tvProductName.text = product.name
        val formattedPrice = "Rp. ${product.price}"
        tvProductPrice.text = formattedPrice

        imageButtonFavorite.setOnClickListener {
            isFavorite = !isFavorite

            if (isFavorite) {
                imageButtonFavorite.setImageResource(R.drawable.baseline_favorite_checked_24)
                sharedViewModel.addToFavoriteList(product)
            } else {
                imageButtonFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                sharedViewModel.removeFromFavoriteList(product)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
