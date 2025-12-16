package com.loiev.geonot.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loiev.geonot.data.GeoNote
import com.loiev.geonot.data.GeoNoteRepository
import com.loiev.geonot.utils.GeofenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class NotesViewModel(private val repository: GeoNoteRepository, application: Application) : AndroidViewModel(application) {
    val notes: StateFlow<List<GeoNote>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val context = getApplication<Application>().applicationContext
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "images/${UUID.randomUUID()}.jpg")
            file.parentFile?.mkdirs()
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Failed to save image", e)
            return null
        }
    }


    fun addNote(name: String, text: String, radius: Int, latitude: Double, longitude: Double, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { saveImageToInternalStorage(it) }
            val newNote = GeoNote(
                name = name, text = text, radius = radius, latitude = latitude,
                longitude = longitude, timestamp = Date().toString(),
                photoPath = imagePath
            )
            repository.insert(newNote)
        }
    }

    fun addNote(name: String, text: String, radius: Int, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val newNote = GeoNote(
                name = name,
                text = text,
                radius = radius,
                latitude = latitude,
                longitude = longitude,
                timestamp = currentDate
            )
            repository.insert(newNote)
        }
    }

    fun registerGeofences(context: Context, notes: List<GeoNote>) {
        val geofenceHelper = GeofenceHelper(context)
        geofenceHelper.addGeofences(notes)
    }


    val totalNotesCount: StateFlow<Int> = repository.totalNotesCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    private val _statsData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val statsData: StateFlow<Map<String, Int>> = _statsData.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = repository.getNotesForStats()
            val stats = processNotesForStats(notes)
            _statsData.value = stats
        }
    }

    fun processNotesForStats(notes: List<GeoNote>): Map<String, Int> {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val weekMap = mutableMapOf<String, Int>()

        for (i in 0..6) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            weekMap[dayFormat.format(calendar.time)] = 0
            calendar.add(Calendar.DAY_OF_YEAR, i)
        }

        val sortedDays = weekMap.keys.sortedWith(compareBy {
            calendar.time = dayFormat.parse(it)
            calendar.get(Calendar.DAY_OF_WEEK)
        })

        val sortedWeekMap = linkedMapOf<String, Int>()
        sortedDays.forEach { sortedWeekMap[it] = 0 }


        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time

        notes.forEach { note ->
            try {
                val noteDate = sdf.parse(note.timestamp)
                if (noteDate != null && noteDate.after(sevenDaysAgo)) {
                    val dayOfWeek = dayFormat.format(noteDate)
                    sortedWeekMap[dayOfWeek] = (sortedWeekMap[dayOfWeek] ?: 0) + 1
                }
            } catch (e: Exception) {
            }
        }
        return sortedWeekMap
    }

    fun updateNote(note: GeoNote, newImageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = newImageUri?.let { saveImageToInternalStorage(it) }
            val updatedNote = note.copy(photoPath = imagePath ?: note.photoPath)
            repository.update(updatedNote)
        }
    }

    suspend fun getNoteById(id: Int): GeoNote? {
        return repository.getNoteById(id)
    }
}