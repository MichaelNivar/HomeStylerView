package com.app.homestyle.repository

import com.app.homestyle.dao.CategoryDao
import com.app.homestyle.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    // Insertar una categoría
    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    // Actualizar una categoría
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    // Eliminar una categoría
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    // Obtener todas las categorías
    suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }

    // Obtener una categoría por su ID
    suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)
    }
}