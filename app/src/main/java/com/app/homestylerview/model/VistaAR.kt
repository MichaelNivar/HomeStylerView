package com.app.homestylerview.model

import java.util.Date

data class VistaAR(
    val usuarioId: String = "",
    val fechaVista: Date = Date(),
    val ubicacionVista: String? = null,
    val configuracionVista: String = ""
)