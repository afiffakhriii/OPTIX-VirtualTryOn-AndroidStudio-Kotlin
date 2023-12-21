package com.afif.optix.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afif.optix.R
import com.afif.optix.data.entity.ProductEntity
import com.bumptech.glide.Glide

class BannerAdapter(
    var originalData: List<ProductEntity>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(productEntity: ProductEntity)
    }

    private var onFavoriteClickListener: (() -> Unit)? = null

    fun setOnFavoriteClickListener(listener: () -> Unit) {
        onFavoriteClickListener = listener
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImageView: ImageView = itemView.findViewById(R.id.banner_image)
        val bannerNameTv: TextView = itemView.findViewById(R.id.tv_banner_product_name)
        val bannerPriceTv: TextView = itemView.findViewById(R.id.tv_banner_price)
        val favoriteButton: Button = itemView.findViewById(R.id.button_favorite_banner)
    }

    var bannerList: List<ProductEntity> = originalData
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        // Inflate layout untuk setiap item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_banner, parent, false)

        return BannerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]

        holder.bannerNameTv.text = banner.name

        holder.bannerPriceTv.text = "Rp. ${banner.price}"

        Glide.with(holder.itemView.context)
            .load(banner.imageUrl)
            .into(holder.bannerImageView)

        holder.itemView.setOnClickListener {
            listener.onItemClick(banner)
        }

        holder.favoriteButton.setOnClickListener {
            onFavoriteClickListener?.invoke()
        }
    }

    fun updateData(newList: List<ProductEntity>) {
        bannerList = newList
    }

    fun restoreOriginalData() {
        bannerList = originalData
    }

    fun filterData(query: String) {
        bannerList = originalData.filter { productEntity ->
            productEntity.name?.contains(query, ignoreCase = true) == true
        }
        notifyDataSetChanged()
    }
}
