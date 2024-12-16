package com.app.homestyle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.homestyle.model.Category
import com.app.homestyle.model.Product
import com.app.homestyle.model.UserAr
import com.app.homestyle.model.UserWithFavorites
import com.app.homestyle.repository.UserProductRepository
import kotlinx.coroutines.launch

class UserProductViewModel(private val repository: UserProductRepository) : ViewModel() {

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _favoriteProducts = MutableLiveData<List<Product>>()
    val favoriteProducts: LiveData<List<Product>> get() = _favoriteProducts

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    fun getUserIdByEmail(email: String) {
        viewModelScope.launch {
            try {
                val id = repository.getUserByEmail(email)?.idUsuario
                _userId.postValue(id)
            } catch (e: Exception) {
                _userId.postValue(null)
                Log.e("UserProductViewModel", "Error al obtener el usuario: ${e.message}")
            }
        }
    }

    fun addFavorite(userId: String, productId: String) {
        viewModelScope.launch {
            try {
                repository.addFavorite(userId, productId)
                _isFavorite.postValue(true) // Actualiza el estado
                Log.d("UserProductViewModel", "Producto añadido a favoritos")
            } catch (e: Exception) {
                Log.e("UserProductViewModel", "Error al añadir a favoritos: ${e.message}")
            }
        }
    }

    fun removeFavorite(userId: String, productId: String) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(userId, productId)
                _isFavorite.postValue(false) // Actualiza el estado
                Log.d("UserProductViewModel", "Producto eliminado de favoritos")
            } catch (e: Exception) {
                Log.e("UserProductViewModel", "Error al eliminar de favoritos: ${e.message}")
            }
        }
    }

    fun checkIfFavorite(userId: String, productId: String) {
        viewModelScope.launch {
            try {
                val favorite = repository.isFavorite(userId, productId)
                _isFavorite.postValue(favorite)
            } catch (e: Exception) {
                _isFavorite.postValue(false)
                Log.e("UserProductViewModel", "Error al verificar favorito: ${e.message}")
            }
        }
    }

    fun getFavoriteProducts(userId: String) {
        viewModelScope.launch {
            try {
                val userWithFavorites = repository.getUserWithFavorites(userId)
                _favoriteProducts.postValue(userWithFavorites.favoriteProducts)
            } catch (e: Exception) {
                _favoriteProducts.postValue(emptyList())
                Log.e("UserProductViewModel", "Error al obtener productos favoritos: ${e.message}")
            }
        }
    }
}

class UserProductViewModelFactory(private val repository: UserProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}