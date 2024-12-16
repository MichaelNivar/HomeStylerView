package com.app.homestyle.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey
    val idProducto: String = "",
    val nombreProducto: String = "",
    val descripcion: String = "",
    val archivoGlbPath: String = "", // Ruta del archivo GLB
    val imagen: ByteArray = byteArrayOf(),
    val categoriaId: String = "",
) : Parcelable
