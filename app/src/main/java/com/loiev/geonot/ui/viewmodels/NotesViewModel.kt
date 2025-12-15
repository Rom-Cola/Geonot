package com.loiev.geonot.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loiev.geonot.data.GeoNote
import com.loiev.geonot.data.GeoNoteRepository
import com.loiev.geonot.utils.GeofenceHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel(private val repository: GeoNoteRepository) : ViewModel() {
    val notes: StateFlow<List<GeoNote>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    fun addNote(name: String, text: String, radius: Int) { // <-- Додали параметр radius
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val newNote = GeoNote(
                name = name,
                text = text,
                radius = radius,
                latitude = 50.4501,
                longitude = 30.5234,
                timestamp = currentDate
            )
            repository.insert(newNote)
        }
    }

    fun registerGeofences(context: Context, notes: List<GeoNote>) {
        val geofenceHelper = GeofenceHelper(context)
        geofenceHelper.addGeofences(notes)
    }
}