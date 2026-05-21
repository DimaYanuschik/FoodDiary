package com.example.fooddiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fooddiary.presentation.screens.water.WaterUiState

@Composable
fun WaterTrackerCard(
    uiState: WaterUiState,
    onAddWater: (Int) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onSetGoal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Вода", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Row {
                    IconButton(onClick = { showGoalDialog = true }) {
                        Icon(Icons.Filled.Tune, contentDescription = "Настроить цель")
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Добавить воду")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val progress = if (uiState.goalMl > 0) (uiState.totalMl.toFloat() / uiState.goalMl).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            Text("${uiState.totalMl} / ${uiState.goalMl} мл", style = MaterialTheme.typography.bodyMedium)

            if (uiState.entries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                uiState.entries.takeLast(3).reversed().forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${entry.amountMl} мл")
                        IconButton(onClick = { onDeleteEntry(entry.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var amount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить воду") },
            text = {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    label = { Text("Объём (мл)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val ml = amount.toIntOrNull() ?: 250
                    onAddWater(ml)
                    showAddDialog = false
                }) { Text("Добавить") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Отмена") } }
        )
    }

    if (showGoalDialog) {
        var newGoal by remember { mutableStateOf(uiState.goalMl.toString()) }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Норма воды (мл)") },
            text = {
                OutlinedTextField(
                    value = newGoal,
                    onValueChange = { newGoal = it.filter { c -> c.isDigit() } },
                    label = { Text("Миллилитров в день") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val goal = newGoal.toIntOrNull() ?: 2000
                    onSetGoal(goal)
                    showGoalDialog = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showGoalDialog = false }) { Text("Отмена") } }
        )
    }
}