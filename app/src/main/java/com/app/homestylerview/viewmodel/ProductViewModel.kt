package com.app.homestylerview.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.homestylerview.model.Product
import com.app.homestylerview.repository.ProductRepository
import kotlinx.coroutines.Dispatchers

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    // Obtener todos los productos
    fun getProducts() = liveData(Dispatchers.IO) {
        emit(repository.getAllProducts())
    }

    // Agregar un producto con subida de archivo .glb e imagen
    fun addProduct(
        product: Product,
        glbUri: Uri?,
        imageUri: Uri?
    ) = liveData(Dispatchers.IO) {
        try {
            val glbUrl = glbUri?.let { repository.uploadGlbFile(product.idProducto, it) }
            val imageUrl = imageUri?.let { repository.uploadImage(product.idProducto, it) }

            val updatedProduct = product.copy(
                archivoGlb = glbUrl ?: "",
                imagen = imageUrl ?: ""
            )

            val result = repository.addProduct(updatedProduct)
            emit(result)
        } catch (e: Exception) {
            emit(false)
        }
    }

    // Actualizar un producto
    fun updateProduct(product: Product) = liveData(Dispatchers.IO) {
        emit(repository.updateProduct(product))
    }

    // Eliminar un producto
    fun deleteProduct(productId: String) = liveData(Dispatchers.IO) {
        emit(repository.deleteProduct(productId))
    }
}