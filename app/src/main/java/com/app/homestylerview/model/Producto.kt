package com.app.homestylerview.model

import java.util.Date

data class Producto(
    val idProducto: String = "",
    val nombreProducto: String = "",
    val descripcion: String = "",
    val archivoGlb: String = "",
    val categoriaId: String = "",
    val fechaSubida: Date = Date()
)
