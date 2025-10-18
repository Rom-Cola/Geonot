package com.loiev.geonot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loiev.geonot.data.GeoNote
import com.loiev.geonot.ui.theme.GeonotTheme

@Composable
fun GeoNoteCard(note: GeoNote, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = note.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeoNoteCardPreview() {
    val previewNote = GeoNote(
        id = 1, name = "КПІ", text = "KPI is my favourite place <3",
        latitude = 50.4501, longitude = 30.5234, radius = 50, timestamp = "5 min ago"
    )
    GeonotTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GeoNoteCard(note = previewNote)
        }
    }
}