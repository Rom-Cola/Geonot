package com.loiev.geonot.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.loiev.geonot.data.GeoNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<GeoNote>>(emptyList())
    val notes: StateFlow<List<GeoNote>> = _notes.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        _notes.value = listOf(
            GeoNote(1, "КПІ", "KPI is my favourite place <3", latitude = 0.0, longitude = 0.0, radius = 50, timestamp = "5 min ago"),
            GeoNote(2, "Пузата Хата", "Їжа смачна", latitude = 0.0, longitude = 0.0, radius = 100, timestamp = "11.02.2024")
        )
    }
}