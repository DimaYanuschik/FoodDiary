package com.example.fooddiary.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

@Composable
fun CircularProgressChart(
    progress: Float, // от 0 до 1
    label: String,
    currentValue: Int,
    targetValue: Int,
    color: Color,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = min(size.width, size.height) / 2 - strokeWidth / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Фоновый круг с градиентом
            val gradient = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.1f),
                    color.copy(alpha = 0.3f)
                )
            )

            // Фоновый круг
            drawCircle(
                brush = gradient,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Прогресс с градиентом
            val progressGradient = Brush.sweepGradient(
                colors = listOf(
                    color,
                    color.copy(alpha = 0.7f),
                    color
                )
            )

            drawArc(
                brush = progressGradient,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$currentValue",
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )

            Text(
                text = "/ $targetValue",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showPercentage) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = color
                )
            }
        }
    }
}