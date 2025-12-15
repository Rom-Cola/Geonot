package com.loiev.geonot.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.loiev.geonot.ui.Screen
import com.loiev.geonot.ui.components.GeoNoteCard
import com.loiev.geonot.ui.viewmodels.MapViewModel
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(viewModel: NotesViewModel, navController: NavController, mapViewModel: MapViewModel) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("All Markers") }) }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(notes) { note ->
                Box(
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            // Просимо MapViewModel перемістити камеру
                            mapViewModel.navigateTo(LatLng(note.latitude, note.longitude))
                            // Переходимо на екран карти
                            navController.navigate(Screen.Map.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        onLongClick = {
                            // Переходимо на екран редагування
                            navController.navigate(Screen.EditNote.createRoute(note.id))
                        }
                    )
                ) {
                    GeoNoteCard(note = note, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }
        }
    }

}