package com.loiev.geonot.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.loiev.geonot.data.GeoNote
import com.loiev.geonot.ui.viewmodels.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
) {
    var note by remember { mutableStateOf<GeoNote?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        coroutineScope.launch {
            note = viewModel.getNoteById(noteId)
            Log.d("EditNoteScreen", "Loaded note: $note")
        }
    }

    var name by remember(note) { mutableStateOf(note?.name ?: "") }
    var text by remember(note) { mutableStateOf(note?.text ?: "") }
    var radius by remember(note) { mutableStateOf(note?.radius?.toFloat() ?: 50f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Marker", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        if (note == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Radius", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = radius,
                        onValueChange = { radius = it },
                        valueRange = 10f..500f,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${radius.toInt()} m",
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // "Притискає" кнопки до низу

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val updatedNote = note!!.copy(
                                name = name,
                                text = text,
                                radius = radius.toInt()
                            )
                            viewModel.updateNote(updatedNote)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}