package com.example.fooddiary.ui.components

import android.app.DatePickerDialog
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun DatePicker(
    initialDate: Date = Date(),
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = initialDate }

    // Используем LaunchedEffect для однократного показа диалога
    LaunchedEffect(Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(selectedDate.time)
                onDismiss()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.apply {
            setTitle("Выберите дату рождения")
            // Максимальная дата - сегодня
            datePicker.maxDate = System.currentTimeMillis()
            // Минимальная дата - 150 лет назад
            val minCalendar = Calendar.getInstance()
            minCalendar.add(Calendar.YEAR, -150)
            datePicker.minDate = minCalendar.timeInMillis

            setButton(DatePickerDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
                onDismiss()
            }
        }

        datePickerDialog.show()
    }
}