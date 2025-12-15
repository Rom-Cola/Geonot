package com.loiev.geonot.ui.screens

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Profile & Stats", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total notes created", style = MaterialTheme.typography.titleMedium)
                    Text(text = totalNotes.toString(), style = MaterialTheme.typography.displayMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Activity in last 7 days", style = MaterialTheme.typography.titleMedium)
            BarChart(data = statsData)
        }

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
    Log.d("BarChart", "Received data for chart: $data")

    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No activity data for the last 7 days.")
        }
        return
    }

    val maxVal = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val density = LocalDensity.current
    val barColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val textPaint = remember(onSurfaceColor) {
        Paint().apply {
            color = onSurfaceColor.toArgb()
            textSize = with(density) { 12.sp.toPx() }
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(bottom = 20.dp)
    ) {
        val spacing = size.width / (data.size * 2 + 1)
        val barWidth = spacing

        val dataList = data.entries.toList()

        dataList.forEachIndexed { index, entry ->
            val (day, count) = entry

            val centerX = spacing * (2 * index + 1.5f)

            val barHeight = if (maxVal > 0) (count.toFloat() / maxVal) * size.height else 0f

            if (barHeight > 0f) {
                drawRect(
                    color = barColor,
                    topLeft = Offset(x = centerX - barWidth / 2, y = size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight)
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                day,
                centerX,
                size.height + with(density) { 16.dp.toPx() },
                textPaint
            )
        }
    }
}