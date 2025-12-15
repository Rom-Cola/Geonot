package com.loiev.geonot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    latitude: Double,
    longitude: Double
) {
    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf(50f) } // Float для Slider

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New marker", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { /* TODO: Implement photo selection */ }) {
                        Text("Set photo")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Поле для назви
            Text("Name", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Enter the name") },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Поле для тексту
            Text("Text", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Enter the text") },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Робимо поле вищим
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("Radius", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 10f..500f, // Діапазон від 10 до 500 метрів
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${radius.toInt()} m",
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 16.sp
                )
            }

            // Використовуємо Spacer, щоб "притиснути" кнопки до низу
            Spacer(modifier = Modifier.weight(1f))

            // Кнопки "Back" та "Save"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addNote(name, text, radius.toInt(), latitude, longitude)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}