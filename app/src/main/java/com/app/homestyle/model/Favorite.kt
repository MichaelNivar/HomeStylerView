package com.app.homestyle.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Favorite(
    @PrimaryKey val idFavorite: String = "",
    val userId: String = "",
    val list: List<Product> = listOf()
)