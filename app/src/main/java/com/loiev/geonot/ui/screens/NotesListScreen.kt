package com.loiev.geonot.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loiev.geonot.data.GeoNote
import com.loiev.geonot.ui.components.GeoNoteCard
import com.loiev.geonot.ui.theme.GeonotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen() {
    val notes = listOf(
        GeoNote(1, "КПІ", "KPI is my favourite place <3", latitude = 0.0, longitude = 0.0, radius = 50, timestamp = "5 min ago"),
        GeoNote(2, "Пузата Хата", "Їжа смачна", latitude = 0.0, longitude = 0.0, radius = 100, timestamp = "11.02.2024")
    )
    Scaffold(topBar = { TopAppBar(title = { Text("All Markers") }) }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(notes) { note ->
                GeoNoteCard(note = note, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}