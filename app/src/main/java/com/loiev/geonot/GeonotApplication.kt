package com.loiev.geonot

import android.app.Application
import com.loiev.geonot.data.AppDatabase
import com.loiev.geonot.data.GeoNoteRepository

class GeonotApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { GeoNoteRepository(database.geoNoteDao()) }
}