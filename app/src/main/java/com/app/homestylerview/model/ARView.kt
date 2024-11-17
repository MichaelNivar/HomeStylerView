package com.app.homestylerview.model

import java.util.Date

data class ARView(
    val usuarioId: String = "",
    val fechaVista: Date = Date(),
    val ubicacionVista: String? = null,
    val configuracionVista: String = ""
)