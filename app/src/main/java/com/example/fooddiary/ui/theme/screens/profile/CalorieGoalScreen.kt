package com.example.fooddiary.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data_old.models.CalorieGoal
import com.example.fooddiary.ui.viewmodels.CalorieGoalViewModel
import com.example.fooddiary.ui.viewmodels.UserProfileViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieGoalScreen(
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit,

    sharedViewModelStoreOwner: ViewModelStoreOwner,
    calorieGoalViewModel: CalorieGoalViewModel = hiltViewModel(sharedViewModelStoreOwner),
    userProfileViewModel: UserProfileViewModel = hiltViewModel(sharedViewModelStoreOwner)
) {
//    val calorieGoalViewModel: CalorieGoalViewModel = viewModel()
//    val userProfileViewModel: UserProfileViewModel = viewModel()

    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val calculatedCalories by userProfileViewModel.calculatedCalories.collectAsState()
    val isLoading by calorieGoalViewModel.isLoading.collectAsState()

    // Состояния для формы
    var dailyCalories by remember {
        mutableStateOf(
            calorieGoal?.dailyCalories?.toString() ?:
            calculatedCalories.toString().takeIf { it != "0" } ?: "2000"
        )
    }

    var proteinPercent by remember {
        mutableStateOf(calorieGoal?.proteinPercentage?.toString() ?: "30")
    }
    var fatPercent by remember {
        mutableStateOf(calorieGoal?.fatPercentage?.toString() ?: "30")
    }
    var carbsPercent by remember {
        mutableStateOf(calorieGoal?.carbsPercentage?.toString() ?: "40")
    }

    // Валидация процентов
    val totalPercent = remember(proteinPercent, fatPercent, carbsPercent) {
        val protein = proteinPercent.toIntOrNull() ?: 0
        val fat = fatPercent.toIntOrNull() ?: 0
        val carbs = carbsPercent.toIntOrNull() ?: 0
        protein + fat + carbs
    }

    val isPercentValid = totalPercent == 100
    val isFormValid = dailyCalories.isNotBlank() &&
            dailyCalories.toIntOrNull() != null &&
            dailyCalories.toInt() > 0 &&
            isPercentValid

    // Рассчитываем граммы
    val calories = dailyCalories.toIntOrNull() ?: 0
    val proteinG = ((calories * (proteinPercent.toIntOrNull() ?: 0) / 100.0) / 4).roundToInt()
    val fatG = ((calories * (fatPercent.toIntOrNull() ?: 0) / 100.0) / 9).roundToInt()
    val carbsG = ((calories * (carbsPercent.toIntOrNull() ?: 0) / 100.0) / 4).roundToInt()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Цели по калориям") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Рекомендованные калории из профиля
            if (calculatedCalories > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Рекомендовано на основе профиля:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "$calculatedCalories ккал/день",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = {
                                dailyCalories = calculatedCalories.toString()
                                // Автоматически устанавливаем проценты по цели
                                userProfile?.goal?.let { goal ->
                                    val (p, f, c) = getDefaultMacroPercentages(goal)
                                    proteinPercent = p.toString()
                                    fatPercent = f.toString()
                                    carbsPercent = c.toString()
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Использовать рекомендацию")
                        }
                    }
                }
            }

            // Суточные калории
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Суточная норма калорий",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dailyCalories,
                        onValueChange = { dailyCalories = it.filter { char -> char.isDigit() } },
                        label = { Text("Калории в день") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null)
                        },
                        suffix = { Text("ккал") }
                    )
                }
            }

            // Распределение БЖУ
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Распределение БЖУ",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Белки
                        OutlinedTextField(
                            value = proteinPercent,
                            onValueChange = {
                                proteinPercent = it.filter { char -> char.isDigit() }
                                // Автоматически корректируем углеводы
                                val protein = proteinPercent.toIntOrNull() ?: 0
                                val fat = fatPercent.toIntOrNull() ?: 0
                                val remaining = 100 - protein - fat
                                if (remaining >= 0) {
                                    carbsPercent = remaining.toString()
                                }
                            },
                            label = { Text("Белки") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !isPercentValid,
                            suffix = { Text("%") }
                        )

                        // Жиры
                        OutlinedTextField(
                            value = fatPercent,
                            onValueChange = {
                                fatPercent = it.filter { char -> char.isDigit() }
                                // Автоматически корректируем углеводы
                                val protein = proteinPercent.toIntOrNull() ?: 0
                                val fat = fatPercent.toIntOrNull() ?: 0
                                val remaining = 100 - protein - fat
                                if (remaining >= 0) {
                                    carbsPercent = remaining.toString()
                                }
                            },
                            label = { Text("Жиры") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !isPercentValid,
                            suffix = { Text("%") }
                        )

                        // Углеводы
                        OutlinedTextField(
                            value = carbsPercent,
                            onValueChange = {
                                carbsPercent = it.filter { char -> char.isDigit() }
                                // Автоматически корректируем жиры
                                val protein = proteinPercent.toIntOrNull() ?: 0
                                val carbs = carbsPercent.toIntOrNull() ?: 0
                                val remaining = 100 - protein - carbs
                                if (remaining >= 0) {
                                    fatPercent = remaining.toString()
                                }
                            },
                            label = { Text("Углеводы") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !isPercentValid,
                            suffix = { Text("%") }
                        )

                        if (!isPercentValid) {
                            Text(
                                text = "Сумма процентов должна быть 100% (сейчас: $totalPercent%)",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Предварительный просмотр
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Предварительный просмотр",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Белки
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Белки:", style = MaterialTheme.typography.bodyMedium)
                            Text("$proteinG г", style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Жиры
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Жиры:", style = MaterialTheme.typography.bodyMedium)
                            Text("$fatG г", style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Углеводы
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Углеводы:", style = MaterialTheme.typography.bodyMedium)
                            Text("$carbsG г", style = MaterialTheme.typography.bodyLarge)
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Итого калорий из БЖУ
                        val calculatedFromMacros = proteinG * 4 + fatG * 9 + carbsG * 4
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Итого из БЖУ:", style = MaterialTheme.typography.bodyMedium)
                            Text("$calculatedFromMacros ккал", style = MaterialTheme.typography.bodyLarge)
                        }

                        if (calculatedFromMacros != calories) {
                            Text(
                                text = "Внимание: калории из БЖУ не совпадают с заданными ($calories ккал)",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Быстрые пресеты
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Быстрые пресеты",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Стандартный
                            FilterChip(
                                selected = proteinPercent == "30" && fatPercent == "30" && carbsPercent == "40",
                                onClick = {
                                    proteinPercent = "30"
                                    fatPercent = "30"
                                    carbsPercent = "40"
                                },
                                label = { Text("Стандартный") }
                            )

                            // Высокобелковый
                            FilterChip(
                                selected = proteinPercent == "40" && fatPercent == "30" && carbsPercent == "30",
                                onClick = {
                                    proteinPercent = "40"
                                    fatPercent = "30"
                                    carbsPercent = "30"
                                },
                                label = { Text("Высокий белок") }
                            )

                            // Низкоуглеводный
                            FilterChip(
                                selected = proteinPercent == "35" && fatPercent == "45" && carbsPercent == "20",
                                onClick = {
                                    proteinPercent = "35"
                                    fatPercent = "45"
                                    carbsPercent = "20"
                                },
                                label = { Text("Низкие углеводы") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка сохранения
                Button(
                    onClick = {
                        val goal = CalorieGoal(
                            dailyCalories = calories,
                            proteinPercentage = proteinPercent.toInt(),
                            fatPercentage = fatPercent.toInt(),
                            carbsPercentage = carbsPercent.toInt()
                        )
                        calorieGoalViewModel.saveCalorieGoal(goal)
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Сохранить цели")
                    }
                }
            }
        }
    }
}

private fun getDefaultMacroPercentages(goal: com.example.fooddiary.data_old.models.Goal): Triple<Int, Int, Int> {
    return when (goal) {
        com.example.fooddiary.data_old.models.Goal.LOSE_WEIGHT -> Triple(40, 30, 30)
        com.example.fooddiary.data_old.models.Goal.MAINTAIN -> Triple(30, 30, 40)
        com.example.fooddiary.data_old.models.Goal.GAIN_WEIGHT -> Triple(25, 25, 50)
    }
}