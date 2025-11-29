package com.loiev.geonot.data

import kotlinx.coroutines.flow.Flow

class GeoNoteRepository(private val geoNoteDao: GeoNoteDao) {
    val allNotes: Flow<List<GeoNote>> = geoNoteDao.getAllNotes()

    suspend fun insert(note: GeoNote) {
        geoNoteDao.insert(note)
    }
}