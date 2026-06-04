package com.example.fooddiary.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // Заголовок — кликабельная строка на всю ширину, как в дневнике питания
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = "Вода",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Вода",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showGoalDialog = true }) {
                    Icon(Icons.Filled.Tune, contentDescription = "Настроить цель")
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Добавить воду")
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть"
                )
            }

            // Прогресс-бар и сводка (видны всегда)
            val progress = if (uiState.goalMl > 0) (uiState.totalMl.toFloat() / uiState.goalMl).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                "${uiState.totalMl} / ${uiState.goalMl} мл",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Разворачиваемый список всех записей
            AnimatedVisibility(visible = expanded) {
                if (uiState.entries.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        uiState.entries.reversed().forEach { entry ->
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
                    Spacer(Modifier.height(8.dp))
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