package com.app.homestyle.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey
    val idCategoria: String = "",
    val nombreCategoria: String = "",
    val imagen: ByteArray = byteArrayOf() // Para almacenar los datos de la imagen
)
