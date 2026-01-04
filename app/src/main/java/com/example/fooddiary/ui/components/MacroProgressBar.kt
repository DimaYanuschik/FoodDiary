package com.example.fooddiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MacroProgressBar(
    current: Double,
    goal: Int,
    label: String,
    color: Color,
    unit: String = "г",
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) current / goal else 0.0
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color
                )

                Text(
                    text = "${String.format("%.1f", current)} / $goal$unit",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = progress.toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$percentage% от цели",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}