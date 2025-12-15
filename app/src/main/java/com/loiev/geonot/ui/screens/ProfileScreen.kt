package com.loiev.geonot.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loiev.geonot.ui.viewmodels.AuthViewModel
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    notesViewModel: NotesViewModel,
    authViewModel: AuthViewModel
) {
    val totalNotes by notesViewModel.totalNotesCount.collectAsState()
    val statsData by notesViewModel.statsData.collectAsState()

    val userEmail = authViewModel.userEmail ?: "No email"
    val userDisplayName = authViewModel.userDisplayName ?: ""

    var firstName by remember { mutableStateOf(userDisplayName.substringBefore(" ")) }
    var lastName by remember { mutableStateOf(userDisplayName.substringAfter(" ", "")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Profile & Stats",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = userEmail,
            onValueChange = {},
            label = { Text("Email") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newDisplayName = "$firstName $lastName".trim()
                authViewModel.updateProfile(newDisplayName)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }

        Divider(modifier = Modifier.padding(vertical = 24.dp))

        Text("Statistics", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total notes created", style = MaterialTheme.typography.titleMedium)
                Text(text = totalNotes.toString(), style = MaterialTheme.typography.displayMedium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Activity in last 7 days", style = MaterialTheme.typography.titleMedium)
        BarChart(data = statsData)

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { authViewModel.signOut() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
fun BarChart(data: Map<String, Int>) {
    if (data.isEmpty()) return

    val maxVal = data.values.maxOrNull() ?: 0
    val density = LocalDensity.current

    val textPaint = android.graphics.Paint().apply {
        color = 0xFFFFFFFF.toInt() // Білий колір
        textSize = with(density) { 12.sp.toPx() }
        textAlign = android.graphics.Paint.Align.CENTER
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val barWidth = size.width / (data.size * 2)
        var currentX = barWidth

        data.forEach { (day, count) ->
            val barHeight = (count.toFloat() / maxVal) * size.height

            drawRect(
                color = Color(0xFFBB86FC),
                topLeft = Offset(x = currentX - barWidth / 2, y = size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight)
            )

            drawContext.canvas.nativeCanvas.drawText(
                day,
                currentX,
                size.height + 40,
                textPaint
            )

            currentX += barWidth * 2
        }
    }
}