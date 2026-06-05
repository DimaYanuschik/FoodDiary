package com.example.fooddiary.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarStrip(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weekStart = getWeekStart(selectedDate)
    val days = mutableListOf<Date>()
    val tempCal = Calendar.getInstance().apply { time = weekStart }
    repeat(7) {
        days.add(tempCal.time)
        tempCal.add(Calendar.DAY_OF_MONTH, 1)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Неделя",
                style = MaterialTheme.typography.labelLarge
            )
            Row {
                TextButton(onClick = { onDateSelected(Date()) }) {
                    Text("Сегодня")
                }
                TextButton(onClick = onCalendarClick) {
                    Text("Календарь")
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { date ->
                val isSelected = isSameDay(date, selectedDate)
                val isToday = isSameDay(date, Date())
                DayButton(
                    date = date,
                    isSelected = isSelected,
                    isToday = isToday,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
private fun DayButton(date: Date, isSelected: Boolean, isToday: Boolean, onClick: () -> Unit) {
    val dayFormat = SimpleDateFormat("E", Locale.getDefault())
    val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    val (backgroundColor, textColor, border) = when {
        isSelected -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        )
        isToday -> Triple(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primary,
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.onSurface,
            null
        )
    }

    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 2.dp),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        border = border,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayFormat.format(date),
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
            Text(
                text = dateFormat.format(date),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

private fun isSameDay(d1: Date, d2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = d1 }
    val cal2 = Calendar.getInstance().apply { time = d2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun getWeekStart(date: Date): Date {
    val cal = Calendar.getInstance().apply { time = date }
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}