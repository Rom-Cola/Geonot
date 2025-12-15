package com.loiev.geonot.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.loiev.geonot.ui.components.GeoNoteCard
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("All Markers") }) }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(notes) { note ->
                GeoNoteCard(note = note, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}