package com.loiev.geonot.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.loiev.geonot.ui.Screen
import com.loiev.geonot.ui.viewmodels.NotesViewModel
import com.loiev.geonot.utils.hasLocationPermission
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: NotesViewModel, navController: NavController) {
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current

    // --- 1. СТАН ТА ЗАПИТ ДОЗВОЛІВ ---

    var hasForegroundPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    var hasBackgroundPermission by remember {
        val initialValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
        mutableStateOf(initialValue)
    }

    Log.d("MapScreen", "Recomposing. Foreground: $hasForegroundPermission, Background: $hasBackgroundPermission")

    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Log.d("MapScreen", "Background permission granted: $isGranted")
            if (isGranted) {
                hasBackgroundPermission = true
            }
        }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            Log.d("MapScreen", "Foreground permission granted: $isGranted")
            if (isGranted) {
                hasForegroundPermission = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d("MapScreen", "Requesting background permission...")
                    backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* ... */ }
    )

    LaunchedEffect(Unit) {
        Log.d("MapScreen", "LaunchedEffect for permissions triggered.")
        if (!hasForegroundPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // --- 2. НАЛАШТУВАННЯ КАРТИ ТА ГЕОЛОКАЦІЇ ---
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.4501, 30.5234), 10f)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(hasForegroundPermission) {
        if (hasForegroundPermission) {
            Log.d("MapScreen", "Foreground permission is available, getting last location.")
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
                        )
                    }
                }
            }
        }
    }

    // --- 3. РЕЄСТРАЦІЯ ГЕОЗОН ---
    LaunchedEffect(notes, hasBackgroundPermission) {
        Log.d("MapScreen", "Geofence registration effect triggered. Notes count: ${notes.size}, Background permission: $hasBackgroundPermission")
        if (hasBackgroundPermission && notes.isNotEmpty()) {
            Log.d("MapScreen", "CONDITION MET: Calling registerGeofences.")
            viewModel.registerGeofences(context, notes)
        }
    }

    // --- 4. ВІДОБРАЖЕННЯ КАРТИ ТА МАРКЕРІВ ---
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasForegroundPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = hasForegroundPermission),
        onMapLongClick = { latLng ->
            navController.navigate(
                Screen.AddNote.createRoute(latLng.latitude, latLng.longitude)
            )
        }
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