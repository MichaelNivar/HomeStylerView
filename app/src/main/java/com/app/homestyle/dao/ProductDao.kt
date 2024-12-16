package com.app.homestyle.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.homestyle.model.Product

@Dao
interface ProductDao {

    // Insertar un producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Actualizar un producto
    @Update
    suspend fun updateProduct(product: Product)

    // Eliminar un producto
    @Delete
    suspend fun deleteProduct(product: Product)

    // Obtener todos los productos
    @Query("SELECT * FROM product_table")
    suspend fun getAllProducts(): List<Product>

    // Obtener un producto por su ID
    @Query("SELECT * FROM product_table WHERE idProducto = :id")
    suspend fun getProductById(id: String): Product?

    // Obtener productos por ID de categoría
    @Query("SELECT * FROM product_table WHERE categoriaId = :categoryId")
    suspend fun getProductsByCategoryId(categoryId: String): List<Product>

    // Obtener productos parecidos según el nombre
    @Query("SELECT * FROM product_table WHERE nombreProducto LIKE '%' || :productName || '%'")
    suspend fun getProductsByName(productName: String): List<Product>

    // Obtener productos por categoría y nombres parecidos
    @Query("""
        SELECT * FROM product_table 
        WHERE categoriaId = :categoryId AND nombreProducto LIKE '%' || :productName || '%'
    """)
    suspend fun getProductsByCategoryAndName(categoryId: String, productName: String): List<Product>
}