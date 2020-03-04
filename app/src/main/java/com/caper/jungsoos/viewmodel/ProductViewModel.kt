package com.caper.jungsoos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.caper.jungsoos.entities.Cart
import com.caper.jungsoos.entities.Product

/**
 * This is the Product View Model Class
 * Contains List of Products
 */
class ProductViewModel : ViewModel() {
    var productList = MutableLiveData<List<Product>>()
}

/**
 * This is the Cart View Model Class
 * Contains Linked Hash Map  {Key - Product  ID, Value Cart}
 */
class CartListViewModel : ViewModel() {
    var cartList = MutableLiveData<LinkedHashMap<String, Cart>>()

    companion object {
        private var instance: CartListViewModel? = null
        fun getInstance() =
            instance ?: synchronized(CartListViewModel::class.java) {
                instance ?: CartListViewModel().also { instance = it }
            }
    }
}

/**
 * This is a View Model Factory for the CartList View Model
 * This will return same instance for this app.
 */
class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(CartListViewModel::class.java) -> CartListViewModel.getInstance()
                else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
            }
        } as T

    companion object {
        private var instance: ViewModelFactory? = null
        fun getInstance() =
            instance ?: synchronized(ViewModelFactory::class.java) {
                instance ?: ViewModelFactory().also { instance = it }
            }
    }
}