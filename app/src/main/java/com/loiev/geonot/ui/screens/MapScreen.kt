package com.loiev.geonot.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.loiev.geonot.ui.viewmodels.NotesViewModel
import com.loiev.geonot.utils.hasLocationPermission
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current

    // Стан для відстеження, чи є дозвіл
    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }

    // Launcher для запиту дозволів. Він оновлює наш стан `hasPermission`.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                hasPermission = true
            }
        }
    )

    // Запитуємо дозвіл при першому вході на екран, якщо його немає
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Стан камери. Починаємо з Києва.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.4501, 30.5234), 10f)
    }

    // Клієнт для отримання геолокації
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    // Цей блок виконається один раз, коли `hasPermission` стане `true`
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    // Плавно переміщуємо камеру на позицію користувача
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
                        )
                    }
                }
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        // Вмикаємо синю точку та кнопку "Моє місцезнаходження" на карті
        properties = MapProperties(isMyLocationEnabled = hasPermission),
        // Вимикаємо стандартну обробку кліків, якщо ми не хочемо, щоб карта реагувала на них
        uiSettings = MapUiSettings(myLocationButtonEnabled = hasPermission)
    ) {
        notes.forEach { note ->
            Marker(
                state = MarkerState(position = LatLng(note.latitude, note.longitude)),
                title = note.name,
                snippet = note.text
            )
        }
    }
}