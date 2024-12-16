package com.app.homestyle.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_table")
data class UserAr(
    @PrimaryKey val idUsuario: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val fechaCreacion: Date = Date(),
    val imagenPerfil: ByteArray? = null
)
