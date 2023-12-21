// FavoriteFragment

package com.afif.optix.ui.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afif.optix.R
import com.afif.optix.data.entity.ProductEntity
import com.afif.optix.ui.adapter.FavoriteAdapter
import com.afif.optix.ui.viewModel.SharedViewModel

class FavoriteFragment : Fragment(), FavoriteAdapter.OnItemClickListener {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        favoriteAdapter = FavoriteAdapter(emptyList(), this)
        view.findViewById<RecyclerView>(R.id.recyclerView_favorite).apply {
            layoutManager = GridLayoutManager(context, 1) // 1 column
            adapter = favoriteAdapter
        }

        sharedViewModel.favoriteList.observe(viewLifecycleOwner, Observer { favorites ->
            favoriteAdapter.updateData(favorites)
        })


        return view
    }

    override fun onItemClick(productEntity: ProductEntity) {
    }
}
