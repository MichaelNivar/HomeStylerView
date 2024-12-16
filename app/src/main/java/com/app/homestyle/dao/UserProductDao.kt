package com.app.homestyle.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.homestyle.model.Product
import com.app.homestyle.model.UserAr
import com.app.homestyle.model.UserProductCrossRef
import com.app.homestyle.model.UserWithFavorites

@Dao
interface UserProductDao {

    // Insertar un usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAr)

    // Insertar un producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Insertar una relación usuario-producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProductCrossRef(crossRef: UserProductCrossRef)

    // Eliminar una relación usuario-producto
    @Delete
    suspend fun deleteUserProductCrossRef(crossRef: UserProductCrossRef)

    // Verificar si un producto es favorito de un usuario
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM UserProductCrossRef
            WHERE idUsuario = :userId AND idProducto = :productId
        )
    """)
    suspend fun isFavorite(userId: String, productId: String): Boolean

    // Obtener un usuario con sus productos favoritos
    @Transaction
    @Query("SELECT * FROM user_table WHERE idUsuario = :userId")
    suspend fun getUserWithFavorites(userId: String): UserWithFavorites

    // Obtener un usuario por su email
    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserAr?
}
