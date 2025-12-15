package com.loiev.geonot.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GeoNoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: GeoNote)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotes(): Flow<List<GeoNote>>

    @Query("SELECT COUNT(*) FROM notes_table")
    fun getTotalNotesCount(): Flow<Int>

    @Query("SELECT * FROM notes_table ORDER BY timestamp DESC")
    fun getAllNotesList(): List<GeoNote>

    @Update
    suspend fun update(note: GeoNote)

    @Query("SELECT * FROM notes_table WHERE id = :id")
    suspend fun getNoteById(id: Int): GeoNote?
}