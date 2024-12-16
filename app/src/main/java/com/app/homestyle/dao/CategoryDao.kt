package com.app.homestyle.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.homestyle.model.Category

@Dao
interface CategoryDao {
    // Insertar una categoría
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    // Actualizar una categoría
    @Update
    suspend fun updateCategory(category: Category)

    // Eliminar una categoría
    @Delete
    suspend fun deleteCategory(category: Category)

    // Obtener todas las categorías
    @Query("SELECT * FROM category_table")
    suspend fun getAllCategories(): List<Category>

    // Obtener una categoría por su ID
    @Query("SELECT * FROM category_table WHERE nombreCategoria = :id")
    suspend fun getCategoryById(id: String): Category?
}