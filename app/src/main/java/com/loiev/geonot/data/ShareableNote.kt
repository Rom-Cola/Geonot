package com.loiev.geonot.data

import kotlinx.serialization.Serializable

@Serializable
data class ShareableNote(
    val name: String,
    val text: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int
)