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
    // Стан для зберігання завантаженої нотатки
    var note by remember { mutableStateOf<GeoNote?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Завантажуємо дані нотатки один раз при першому запуску екрану
    LaunchedEffect(noteId) {
        coroutineScope.launch {
            note = viewModel.getNoteById(noteId)
            Log.d("EditNoteScreen", "Loaded note: $note")
        }
    }

    // Стани для полів вводу.
    // `remember(note)` гарантує, що вони оновляться, коли `note` завантажиться.
    var name by remember(note) { mutableStateOf(note?.name ?: "") }
    var text by remember(note) { mutableStateOf(note?.text ?: "") }
    var radius by remember(note) { mutableStateOf(note?.radius?.toFloat() ?: 50f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Marker", fontWeight = FontWeight.Bold) }
                // Можна додати кнопку "Назад", якщо потрібно
            )
        }
    ) { paddingValues ->
        // Показуємо індикатор завантаження, поки дані нотатки не отримано
        if (note == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Коли дані завантажено, показуємо основний контент
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Поле для назви
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Поле для тексту
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Секція для радіусу
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

                // Кнопки "Back" та "Save"
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
                            // Створюємо оновлений об'єкт і зберігаємо його
                            val updatedNote = note!!.copy(
                                name = name,
                                text = text,
                                radius = radius.toInt()
                            )
                            viewModel.updateNote(updatedNote)
                            navController.popBackStack() // Повертаємось на попередній екран
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