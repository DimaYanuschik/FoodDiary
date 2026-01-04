package com.example.fooddiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalorieProgressBar(
    current: Int,
    goal: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) current.toFloat() / goal else 0f
    val percentage = (progress * 100).toInt()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "$current / $goal ккал ($percentage%)",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth(),
            color = when {
                progress < 0.7 -> MaterialTheme.colorScheme.primary
                progress < 0.9 -> MaterialTheme.colorScheme.secondary
                progress < 1.0 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.error
            }
        )
    }
}