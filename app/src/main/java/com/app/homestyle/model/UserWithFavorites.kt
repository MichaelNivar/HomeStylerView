package com.app.homestyle.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithFavorites(
    @Embedded val user: UserAr,
    @Relation(
        parentColumn = "idUsuario",
        entityColumn = "idProducto",
        associateBy = Junction(UserProductCrossRef::class)
    )
    val favoriteProducts: List<Product>
)

