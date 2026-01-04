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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyProgressChart(
    dailyData: List<Pair<Date, Int>>, // Дата -> калории
    goal: Int,
    modifier: Modifier = Modifier
) {
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

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
                text = "Недельный прогресс",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Заполняем данные за неделю
                val weekData = MutableList(7) { 0 }
                dailyData.forEach { (date, calories) ->
                    val calendar = Calendar.getInstance().apply { time = date }
                    val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Воскресенье -> 0
                    if (dayOfWeek in 0..6) {
                        weekData[dayOfWeek] = calories
                    }
                }

                weekData.forEachIndexed { index, calories ->
                    val dayLabel = weekDays[index]
                    val height = if (goal > 0) {
                        120.dp * (calories.toFloat() / goal).coerceIn(0f, 1.5f)
                    } else {
                        0.dp
                    }

                    val color = if (calories > goal) {
                        MaterialTheme.colorScheme.error
                    } else if (calories > goal * 0.9) {
                        MaterialTheme.colorScheme.tertiary
                    } else if (calories > goal * 0.7) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Линия тренда
                        Box(
                            modifier = Modifier
                                .height(height)
                                .width(24.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = color,
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            // Значение внутри столбца
                            if (height > 24.dp && calories > 0) {
                                Text(
                                    text = calories.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Индикатор сегодняшнего дня
                        val today = Calendar.getInstance()
                        val todayDayOfWeek = (today.get(Calendar.DAY_OF_WEEK) + 5) % 7
                        if (index == todayDayOfWeek) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Норма"
                )
                LegendItem(
                    color = MaterialTheme.colorScheme.secondary,
                    label = "Почти"
                )
                LegendItem(
                    color = MaterialTheme.colorScheme.tertiary,
                    label = "Приближ."
                )
                LegendItem(
                    color = MaterialTheme.colorScheme.error,
                    label = "Превыш."
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}