package com.afif.optix.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afif.optix.R
import com.afif.optix.data.entity.ProductEntity
import com.bumptech.glide.Glide

class FavoriteAdapter(
    private var favoriteList: List<ProductEntity>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(productEntity: ProductEntity)
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val favoriteImageView: ImageView = itemView.findViewById(R.id.favorite_image)
        val favoriteNameTv: TextView = itemView.findViewById(R.id.tv_favorite_product_name)
        val favoritePriceTv: TextView = itemView.findViewById(R.id.tv_favorite_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_favorite, parent, false)

        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]

        holder.favoriteNameTv.text = favorite.name
        holder.favoritePriceTv.text = "Rp. ${favorite.price}"

        Glide.with(holder.itemView.context)
            .load(favorite.imageUrl)
            .into(holder.favoriteImageView)

        holder.itemView.setOnClickListener {
            listener.onItemClick(favorite)
        }
    }

    fun updateData(newList: List<ProductEntity>) {
        favoriteList = newList
        notifyDataSetChanged()
    }
}
