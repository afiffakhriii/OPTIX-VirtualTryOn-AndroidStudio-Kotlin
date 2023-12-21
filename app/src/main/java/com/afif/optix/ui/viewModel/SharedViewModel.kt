package com.afif.optix.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afif.optix.data.entity.ProductEntity

class SharedViewModel : ViewModel() {

    private val _selectedProduct = MutableLiveData<ProductEntity>()
    val selectedProduct: LiveData<ProductEntity>
        get() = _selectedProduct

    private val _favoriteList = MutableLiveData<List<ProductEntity>>(emptyList())
    val favoriteList: LiveData<List<ProductEntity>>
        get() = _favoriteList

    fun setSelectedProduct(product: ProductEntity) {
        _selectedProduct.value = product
    }

    fun addToFavoriteList(product: ProductEntity) {
        val currentList = _favoriteList.value.orEmpty().toMutableList()
        currentList.add(product)
        _favoriteList.value = currentList
    }

    fun removeFromFavoriteList(product: ProductEntity) {
        val currentList = _favoriteList.value.orEmpty().toMutableList()
        currentList.remove(product)
        _favoriteList.value = currentList
    }
}
