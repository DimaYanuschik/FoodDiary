package com.example.fooddiary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    // Определяем неделю (пн-вс), в которой находится selectedDate
    val weekStart = getWeekStart(selectedDate)
    val days = mutableListOf<Date>()
    val tempCal = Calendar.getInstance().apply { time = weekStart }
    repeat(7) {
        days.add(tempCal.time)
        tempCal.add(Calendar.DAY_OF_MONTH, 1)
    }

    Column(modifier = modifier) {
        // Заголовок недели
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
                TextButton(onClick = {
                    // Сброс на сегодня
                    onDateSelected(Date())
                }) {
                    Text("Сегодня")
                }
                TextButton(onClick = onCalendarClick) {
                    Text("Календарь")
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
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
    val dayFormat = SimpleDateFormat("E", Locale.getDefault()) // Пн, Вт...
    val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    val color = if (isSelected) MaterialTheme.colorScheme.primary
    else if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    else MaterialTheme.colorScheme.onSurface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(text = dayFormat.format(date), style = MaterialTheme.typography.labelSmall, color = color)
        Text(text = dateFormat.format(date), style = MaterialTheme.typography.bodyLarge, color = color)
        if (isToday) {
            Surface(
                modifier = Modifier.size(6.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {}
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