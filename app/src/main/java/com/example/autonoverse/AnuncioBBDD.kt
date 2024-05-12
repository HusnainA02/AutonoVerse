package com.example.autonoverse

import java.text.SimpleDateFormat
import java.util.Locale

data class AnuncioBBDD(
    var referencia: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var experiencia: String = "",
    var correoUsuario: String = "",
    var idUsuario: String = "",
    var contactoUsuario: String = "",
    var ciudadAnuncio: String = "",
    var precioAnuncio: Double = 0.0,
    var disponibilidadAnuncio: Int = 0,
    var timestamp: Long = 0L

) {
    fun getFecha(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
