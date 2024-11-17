package com.app.homestylerview.model

import java.util.Date

data class UserAr(
    val idUsuario: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val fechaCreacion: Date = Date(),
)