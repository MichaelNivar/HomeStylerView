package com.app.homestyle.repository

import com.app.homestyle.dao.ProductDao
import com.app.homestyle.model.Product

class ProductRepository(private val productDao: ProductDao) {

    // Insertar un producto
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    // Actualizar un producto
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    // Eliminar un producto
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    // Obtener todos los productos
    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    // Obtener un producto por su ID
    suspend fun getProductById(id: String): Product? {
        return productDao.getProductById(id)
    }

    // Obtener productos por categoría
    suspend fun getProductsByCategoryId(categoryId: String): List<Product> {
        return productDao.getProductsByCategoryId(categoryId)
    }

    // Obtener productos parecidos según el nombre
    suspend fun getProductsByName(productName: String): List<Product> {
        return productDao.getProductsByName(productName)
    }

    // Obtener productos por categoría y nombres parecidos
    suspend fun getProductsByCategoryAndName(categoryId: String, productName: String): List<Product> {
        return productDao.getProductsByCategoryAndName(categoryId, productName)
    }
}