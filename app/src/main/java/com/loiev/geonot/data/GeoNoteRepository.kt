package com.loiev.geonot.data

import kotlinx.coroutines.flow.Flow

class GeoNoteRepository(private val geoNoteDao: GeoNoteDao) {
    val allNotes: Flow<List<GeoNote>> = geoNoteDao.getAllNotes()

    val totalNotesCount: Flow<Int> = geoNoteDao.getTotalNotesCount()

    suspend fun insert(note: GeoNote) {
        geoNoteDao.insert(note)
    }

    @androidx.annotation.WorkerThread
    suspend fun getNotesForStats(): List<GeoNote> {
        return geoNoteDao.getAllNotesList()
    }
}