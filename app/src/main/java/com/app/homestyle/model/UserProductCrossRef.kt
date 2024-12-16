package com.app.homestyle.model

import androidx.room.Entity

@Entity(primaryKeys = ["idUsuario", "idProducto"])
data class UserProductCrossRef(
    val idUsuario: String,
    val idProducto: String
)
