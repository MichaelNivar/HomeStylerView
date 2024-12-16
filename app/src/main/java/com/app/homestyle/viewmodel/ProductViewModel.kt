package com.app.homestyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.homestyle.model.Product
import com.app.homestyle.repository.ProductRepository
import com.app.homestyle.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> get() = _allProducts
    val productsEvent = SingleLiveEvent<List<Product>>()

    // Insertar un producto y recargar los productos
    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
            fetchAllProducts() // Recarga los productos después de insertar
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            val products = repository.getAllProducts()
            productsEvent.value = products
            _allProducts.postValue(products)
        }
    }

    fun getProductById(id: String, onResult: (Product?) -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductById(id)
            onResult(product)
        }
    }

    fun getProductsByCategoryId(categoryId: String) {
        viewModelScope.launch {
            val products = repository.getProductsByCategoryId(categoryId)
            _allProducts.postValue(products)
        }
    }

    fun searchByName(productName: String) {
        viewModelScope.launch {
            val products = repository.getProductsByName(productName)
            _allProducts.postValue(products)
        }
    }

    fun searchByCategoryAndName(categoryId: String, productName: String) {
        viewModelScope.launch {
            val products = repository.getProductsByCategoryAndName(categoryId, productName)
            _allProducts.postValue(products)
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}