package com.loiev.geonot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class GeoNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val text: String,
    val photoPath: String? = null,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val timestamp: String
)
