package com.loiev.geonot.data

data class GeoNote(
    val id: Int,
    val name: String,
    val text: String,
    val photoPath: String? = null,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val timestamp: String
)