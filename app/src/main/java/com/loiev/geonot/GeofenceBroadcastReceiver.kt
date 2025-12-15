package com.loiev.geonot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.loiev.geonot.data.GeoNoteRepository
import com.loiev.geonot.utils.showGeofenceNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Error receiving geofence event.")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val geofenceId = geofencingEvent.triggeringGeofences?.firstOrNull()?.requestId
            if (geofenceId != null) {
                val application = context.applicationContext as GeonotApplication
                findNoteAndShowNotification(application.repository, geofenceId, context)
            }
        } else if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("GeofenceReceiver", "Geofence Exited!")
        }
    }

    private fun findNoteAndShowNotification(repository: GeoNoteRepository, geofenceId: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val note = repository.allNotes.firstOrNull()?.find { it.id.toString() == geofenceId }
            if (note != null) {
                showGeofenceNotification(context, geofenceId, note.name, note.text)
            }
        }
    }
}