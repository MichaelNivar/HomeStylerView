package com.app.homestyle.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Capture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val imagePath: String // Ruta al archivo en el almacenamiento
)
