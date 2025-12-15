package com.loiev.geonot.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.loiev.geonot.GeofenceBroadcastReceiver
import com.loiev.geonot.data.GeoNote

class GeofenceHelper(private val context: Context) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val mutabilityFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or mutabilityFlag
        )
    }

    @SuppressLint("MissingPermission")
    fun addGeofences(notes: List<GeoNote>) {
        Log.d("GeofenceHelper", "addGeofences called with ${notes.size} notes.")

        if (notes.isEmpty()) {
            Log.w("GeofenceHelper", "Note list is empty, aborting registration.")
            return
        }
        if (!hasLocationPermission(context)) {
            Log.e("GeofenceHelper", "Location permission is NOT granted, aborting registration.")
            return
        }

        val geofenceList = notes.map { note ->
            Geofence.Builder()
                .setRequestId(note.id.toString())
                .setCircularRegion(note.latitude, note.longitude, note.radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        }

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        Log.d("GeofenceHelper", "Attempting to add ${geofenceList.size} geofences...")
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i("GeofenceHelper", "Geofences added successfully!")
            }
            addOnFailureListener { exception ->
                Log.e("GeofenceHelper", "!!! FAILED to add geofences: ${exception.message}", exception)
            }
        }
    }
}