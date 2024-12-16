package com.app.homestyle.repository

import com.app.homestyle.dao.UserProductDao
import com.app.homestyle.model.Product
import com.app.homestyle.model.UserAr
import com.app.homestyle.model.UserProductCrossRef
import com.app.homestyle.model.UserWithFavorites

class UserProductRepository(private val userProductDao: UserProductDao) {

    // Insertar un usuario
    suspend fun insertUser(user: UserAr) {
        userProductDao.insertUser(user)
    }

    // Insertar un producto
    suspend fun insertProduct(product: Product) {
        userProductDao.insertProduct(product)
    }

    // Agregar un producto como favorito para un usuario
    suspend fun addFavorite(userId: String, productId: String) {
        val crossRef = UserProductCrossRef(userId, productId)
        userProductDao.insertUserProductCrossRef(crossRef)
    }

    // Eliminar un producto de favoritos para un usuario
    suspend fun removeFavorite(userId: String, productId: String) {
        val crossRef = UserProductCrossRef(userId, productId)
        userProductDao.deleteUserProductCrossRef(crossRef)
    }

    // Verificar si un producto es favorito de un usuario
    suspend fun isFavorite(userId: String, productId: String): Boolean {
        return userProductDao.isFavorite(userId, productId)
    }

    // Obtener un usuario con su lista de productos favoritos
    suspend fun getUserWithFavorites(userId: String): UserWithFavorites {
        return userProductDao.getUserWithFavorites(userId)
    }

    // Obtener un usuario por su email
    suspend fun getUserByEmail(email: String): UserAr? {
        return userProductDao.getUserByEmail(email)
    }
}
