package com.afif.optix.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afif.optix.R
import com.afif.optix.data.entity.ProductEntity
import com.afif.optix.databinding.FragmentHomeBinding
import com.afif.optix.ui.activity.detail.DetailActivity
import com.afif.optix.ui.activity.tryOn.VirtualTryOnActivity
import com.afif.optix.ui.adapter.BannerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSearchView()

        recyclerView = root.findViewById(R.id.recyclerView_banner)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        bannerAdapter = BannerAdapter(emptyList(), object : BannerAdapter.OnItemClickListener {
            override fun onItemClick(productEntity: ProductEntity) {
                openDetailActivity(productEntity)
            }
        })

        recyclerView.adapter = bannerAdapter

        retrieveDataFromFirestore()

        return root
    }

    private fun setupSearchView() {
        binding.searchView.queryHint = getString(R.string.searchbar_hint)

        binding.searchView.setIconified(true)

        binding.searchView.setOnSearchClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText.orEmpty())
                return true
            }
        })
    }

    private fun retrieveDataFromFirestore() {
        db = FirebaseFirestore.getInstance()
        db.collection("products")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, _ ->
                querySnapshot?.let { result ->
                    val productEntities = result.documents.map { document ->
                        // Extract data from the document
                        val imageUrl = document.getString("Image")
                        val name = document.getString("Name")
                        val description = document.getString("Description")
                        val price = document.getLong("Price")?.toInt() ?: 0

                        ProductEntity(imageUrl, name, price.toLong(), description)
                    }

                    bannerAdapter.originalData = productEntities

                    filterData(binding.searchView.query.toString())
                }
            }
    }

    private fun filterData(query: String) {
        bannerAdapter.filterData(query)
    }

    private fun openDetailActivity(productEntity: ProductEntity) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("PRODUCT_IMAGE", productEntity.imageUrl)
        intent.putExtra("PRODUCT_NAME", productEntity.name)
        intent.putExtra("PRODUCT_PRICE", productEntity.price)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerAdapter.setOnFavoriteClickListener {
            openVirtualTryOnActivity()
        }
    }

    private fun openVirtualTryOnActivity() {
        val intent = Intent(requireContext(), VirtualTryOnActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
