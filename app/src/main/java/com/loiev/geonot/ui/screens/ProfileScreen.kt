package com.loiev.geonot.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loiev.geonot.ui.viewmodels.NotesViewModel

@Composable
fun ProfileScreen(viewModel: NotesViewModel) {
    val totalNotes by viewModel.totalNotesCount.collectAsState()
    val statsData by viewModel.statsData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Statistics", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // KPI-індикатор
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total notes created", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = totalNotes.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Графік
        Text("Activity in last 7 days", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        BarChart(data = statsData)
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

            // Малюємо стовпець
            drawRect(
                color = Color(0xFFBB86FC),
                topLeft = Offset(x = currentX - barWidth / 2, y = size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight)
            )

            // Малюємо підпис дня тижня
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