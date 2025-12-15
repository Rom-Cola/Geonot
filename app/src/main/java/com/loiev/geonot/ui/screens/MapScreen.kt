package com.loiev.geonot.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@Composable
fun MapScreen(viewModel: NotesViewModel) { // <-- 1. Приймаємо ViewModel як параметр
    // 2. Підписуємося на список нотаток з ViewModel
    val notes by viewModel.notes.collectAsState()

    // Початкова позиція камери (наприклад, центр Києва)
    val kyiv = LatLng(50.4501, 30.5234)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kyiv, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 3. Проходимо по списку нотаток і створюємо маркер для кожної
        notes.forEach { note ->
            Marker(
                state = MarkerState(position = LatLng(note.latitude, note.longitude)),
                title = note.name,
                snippet = note.text
                // При кліку на маркер автоматично з'явиться стандартне вікно з title та snippet
            )
        }
    }
}