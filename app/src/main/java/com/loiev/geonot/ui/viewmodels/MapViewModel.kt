package com.loiev.geonot.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _cameraTarget = MutableStateFlow<LatLng?>(null)
    val cameraTarget = _cameraTarget.asStateFlow()

    fun navigateTo(latLng: LatLng) {
        _cameraTarget.value = latLng
    }

    fun onNavigationComplete() {
        _cameraTarget.value = null
    }
}