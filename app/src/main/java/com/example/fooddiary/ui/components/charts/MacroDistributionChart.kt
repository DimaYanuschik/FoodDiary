package com.example.fooddiary.ui.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



@Composable
fun MacroDistributionChart(
    protein: Float,
    fat: Float,
    carbs: Float,
    proteinGoal: Int,
    fatGoal: Int,
    carbsGoal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Распределение БЖУ",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Белки
                MacroColumn(
                    value = protein,
                    goal = proteinGoal,
                    label = "Белки",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )

                // Жиры
                MacroColumn(
                    value = fat,
                    goal = fatGoal,
                    label = "Жиры",
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )

                // Углеводы
                MacroColumn(
                    value = carbs,
                    goal = carbsGoal,
                    label = "Углеводы",
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Детальная информация
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientInfo(
                    label = "Белки",
                    value = protein.toInt(),
                    goal = proteinGoal,
                    unit = "г",
                    color = Color(0xFF4CAF50)
                )
                NutrientInfo(
                    label = "Жиры",
                    value = fat.toInt(),
                    goal = fatGoal,
                    unit = "г",
                    color = Color(0xFF2196F3)
                )
                NutrientInfo(
                    label = "Углеводы",
                    value = carbs.toInt(),
                    goal = carbsGoal,
                    unit = "г",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun MacroColumn(
    value: Float,
    goal: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxHeight = 120.dp
    val height = if (goal > 0) {
        maxHeight * (value / goal).coerceIn(0f, 1.5f)
    } else {
        0.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(height)
                .width(32.dp)
                .padding(horizontal = 4.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = color,
                        shape = MaterialTheme.shapes.medium
                    )
            )

            // Значение внутри столбца
            if (height > 24.dp) {
                Text(
                    text = value.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (goal > 0) {
            Text(
                text = "${((value / goal) * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun NutrientInfo(
    label: String,
    value: Int,
    goal: Int,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Text(
            text = "$value$unit",
            style = MaterialTheme.typography.bodyMedium
        )
        if (goal > 0) {
            Text(
                text = "из $goal$unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}