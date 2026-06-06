package com.example.fooddiary.ui.screens.profile

import androidx.compose.foundation.BorderStroke
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CalorieGoalScreen(
//    onComplete: () -> Unit,
//    onNavigateBack: () -> Unit,
//
//    sharedViewModelStoreOwner: ViewModelStoreOwner,
//    calorieGoalViewModel: CalorieGoalViewModel = hiltViewModel(sharedViewModelStoreOwner),
//    userProfileViewModel: UserProfileViewModel = hiltViewModel(sharedViewModelStoreOwner)
//) {
////    val calorieGoalViewModel: CalorieGoalViewModel = viewModel()
////    val userProfileViewModel: UserProfileViewModel = viewModel()
//
//    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
//    val userProfile by userProfileViewModel.userProfile.collectAsState()
//    val calculatedCalories by userProfileViewModel.calculatedCalories.collectAsState()
//    val isLoading by calorieGoalViewModel.isLoading.collectAsState()
//
//    // Состояния для формы
//    var dailyCalories by remember {
//        mutableStateOf(
//            calorieGoal?.dailyCalories?.toString() ?:
//            calculatedCalories.toString().takeIf { it != "0" } ?: "2000"
//        )
//    }
//
//    var proteinPercent by remember {
//        mutableStateOf(calorieGoal?.proteinPercentage?.toString() ?: "30")
//    }
//    var fatPercent by remember {
//        mutableStateOf(calorieGoal?.fatPercentage?.toString() ?: "30")
//    }
//    var carbsPercent by remember {
//        mutableStateOf(calorieGoal?.carbsPercentage?.toString() ?: "40")
//    }
//
//    // Валидация процентов
//    val totalPercent = remember(proteinPercent, fatPercent, carbsPercent) {
//        val protein = proteinPercent.toIntOrNull() ?: 0
//        val fat = fatPercent.toIntOrNull() ?: 0
//        val carbs = carbsPercent.toIntOrNull() ?: 0
//        protein + fat + carbs
//    }
//
//    val isPercentValid = totalPercent == 100
//    val isFormValid = dailyCalories.isNotBlank() &&
//            dailyCalories.toIntOrNull() != null &&
//            dailyCalories.toInt() > 0 &&
//            isPercentValid
//
//    // Рассчитываем граммы
//    val calories = dailyCalories.toIntOrNull() ?: 0
//    val proteinG = ((calories * (proteinPercent.toIntOrNull() ?: 0) / 100.0) / 4).roundToInt()
//    val fatG = ((calories * (fatPercent.toIntOrNull() ?: 0) / 100.0) / 9).roundToInt()
//    val carbsG = ((calories * (carbsPercent.toIntOrNull() ?: 0) / 100.0) / 4).roundToInt()
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Цели по калориям") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Рекомендованные калории из профиля
//            if (calculatedCalories > 0) {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer
//                    )
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = "Рекомендовано на основе профиля:",
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                        Text(
//                            text = "$calculatedCalories ккал/день",
//                            style = MaterialTheme.typography.titleLarge,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                        Button(
//                            onClick = {
//                                dailyCalories = calculatedCalories.toString()
//                                // Автоматически устанавливаем проценты по цели
//                                userProfile?.goal?.let { goal ->
//                                    val (p, f, c) = getDefaultMacroPercentages(goal)
//                                    proteinPercent = p.toString()
//                                    fatPercent = f.toString()
//                                    carbsPercent = c.toString()
//                                }
//                            },
//                            modifier = Modifier.padding(top = 8.dp)
//                        ) {
//                            Text("Использовать рекомендацию")
//                        }
//                    }
//                }
//            }
//
//            // Суточные калории
//            Card(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = "Суточная норма калорий",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    OutlinedTextField(
//                        value = dailyCalories,
//                        onValueChange = { dailyCalories = it.filter { char -> char.isDigit() } },
//                        label = { Text("Калории в день") },
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        leadingIcon = {
//                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null)
//                        },
//                        suffix = { Text("ккал") }
//                    )
//                }
//            }
//
//            // Распределение БЖУ
//            Card(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = "Распределение БЖУ",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // Белки
//                        OutlinedTextField(
//                            value = proteinPercent,
//                            onValueChange = {
//                                proteinPercent = it.filter { char -> char.isDigit() }
//                                // Автоматически корректируем углеводы
//                                val protein = proteinPercent.toIntOrNull() ?: 0
//                                val fat = fatPercent.toIntOrNull() ?: 0
//                                val remaining = 100 - protein - fat
//                                if (remaining >= 0) {
//                                    carbsPercent = remaining.toString()
//                                }
//                            },
//                            label = { Text("Белки") },
//                            modifier = Modifier.weight(1f),
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            isError = !isPercentValid,
//                            suffix = { Text("%") }
//                        )
//
//                        // Жиры
//                        OutlinedTextField(
//                            value = fatPercent,
//                            onValueChange = {
//                                fatPercent = it.filter { char -> char.isDigit() }
//                                // Автоматически корректируем углеводы
//                                val protein = proteinPercent.toIntOrNull() ?: 0
//                                val fat = fatPercent.toIntOrNull() ?: 0
//                                val remaining = 100 - protein - fat
//                                if (remaining >= 0) {
//                                    carbsPercent = remaining.toString()
//                                }
//                            },
//                            label = { Text("Жиры") },
//                            modifier = Modifier.weight(1f),
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            isError = !isPercentValid,
//                            suffix = { Text("%") }
//                        )
//
//                        // Углеводы
//                        OutlinedTextField(
//                            value = carbsPercent,
//                            onValueChange = {
//                                carbsPercent = it.filter { char -> char.isDigit() }
//                                // Автоматически корректируем жиры
//                                val protein = proteinPercent.toIntOrNull() ?: 0
//                                val carbs = carbsPercent.toIntOrNull() ?: 0
//                                val remaining = 100 - protein - carbs
//                                if (remaining >= 0) {
//                                    fatPercent = remaining.toString()
//                                }
//                            },
//                            label = { Text("Углеводы") },
//                            modifier = Modifier.weight(1f),
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            isError = !isPercentValid,
//                            suffix = { Text("%") }
//                        )
//
//                        if (!isPercentValid) {
//                            Text(
//                                text = "Сумма процентов должна быть 100% (сейчас: $totalPercent%)",
//                                color = MaterialTheme.colorScheme.error,
//                                style = MaterialTheme.typography.labelSmall,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
//                    }
//                }
//
//                // Предварительный просмотр
//                Card(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                    ) {
//                        Text(
//                            text = "Предварительный просмотр",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        // Белки
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Белки:", style = MaterialTheme.typography.bodyMedium)
//                            Text("$proteinG г", style = MaterialTheme.typography.bodyLarge)
//                        }
//
//                        Spacer(modifier = Modifier.height(4.dp))
//
//                        // Жиры
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Жиры:", style = MaterialTheme.typography.bodyMedium)
//                            Text("$fatG г", style = MaterialTheme.typography.bodyLarge)
//                        }
//
//                        Spacer(modifier = Modifier.height(4.dp))
//
//                        // Углеводы
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Углеводы:", style = MaterialTheme.typography.bodyMedium)
//                            Text("$carbsG г", style = MaterialTheme.typography.bodyLarge)
//                        }
//
//                        Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//                        // Итого калорий из БЖУ
//                        val calculatedFromMacros = proteinG * 4 + fatG * 9 + carbsG * 4
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Итого из БЖУ:", style = MaterialTheme.typography.bodyMedium)
//                            Text("$calculatedFromMacros ккал", style = MaterialTheme.typography.bodyLarge)
//                        }
//
//                        if (calculatedFromMacros != calories) {
//                            Text(
//                                text = "Внимание: калории из БЖУ не совпадают с заданными ($calories ккал)",
//                                color = MaterialTheme.colorScheme.error,
//                                style = MaterialTheme.typography.labelSmall,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
//                    }
//                }
//
//                // Быстрые пресеты
//                Card(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                    ) {
//                        Text(
//                            text = "Быстрые пресеты",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            // Стандартный
//                            FilterChip(
//                                selected = proteinPercent == "30" && fatPercent == "30" && carbsPercent == "40",
//                                onClick = {
//                                    proteinPercent = "30"
//                                    fatPercent = "30"
//                                    carbsPercent = "40"
//                                },
//                                label = { Text("Стандартный") }
//                            )
//
//                            // Высокобелковый
//                            FilterChip(
//                                selected = proteinPercent == "40" && fatPercent == "30" && carbsPercent == "30",
//                                onClick = {
//                                    proteinPercent = "40"
//                                    fatPercent = "30"
//                                    carbsPercent = "30"
//                                },
//                                label = { Text("Высокий белок") }
//                            )
//
//                            // Низкоуглеводный
//                            FilterChip(
//                                selected = proteinPercent == "35" && fatPercent == "45" && carbsPercent == "20",
//                                onClick = {
//                                    proteinPercent = "35"
//                                    fatPercent = "45"
//                                    carbsPercent = "20"
//                                },
//                                label = { Text("Низкие углеводы") }
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Кнопка сохранения
//                Button(
//                    onClick = {
//                        val goal = CalorieGoal(
//                            dailyCalories = calories,
//                            proteinPercentage = proteinPercent.toInt(),
//                            fatPercentage = fatPercent.toInt(),
//                            carbsPercentage = carbsPercent.toInt()
//                        )
//                        calorieGoalViewModel.saveCalorieGoal(goal)
//                        onComplete()
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = isFormValid && !isLoading
//                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(16.dp),
//                            strokeWidth = 2.dp
//                        )
//                    } else {
//                        Text("Сохранить цели")
//                    }
//                }
//            }
//        }
//    }
//}

private fun getDefaultMacroPercentages(goal: com.example.fooddiary.data_old.models.Goal): Triple<Int, Int, Int> {
    return when (goal) {
        com.example.fooddiary.data_old.models.Goal.LOSE_WEIGHT -> Triple(40, 30, 30)
        com.example.fooddiary.data_old.models.Goal.MAINTAIN -> Triple(30, 30, 40)
        com.example.fooddiary.data_old.models.Goal.GAIN_WEIGHT -> Triple(25, 25, 50)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieGoalScreen(
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    calorieGoalViewModel: CalorieGoalViewModel = hiltViewModel(sharedViewModelStoreOwner),
    userProfileViewModel: UserProfileViewModel = hiltViewModel(sharedViewModelStoreOwner)
) {
    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val calculatedCalories by userProfileViewModel.calculatedCalories.collectAsState()
    val isLoading by calorieGoalViewModel.isLoading.collectAsState()

    // Состояния для каждого поля и режима
    var dailyCalories by remember { mutableStateOf(calorieGoal?.dailyCalories?.toString() ?: calculatedCalories.toString().takeIf { it != "0" } ?: "2000") }

    // Режимы для каждого макронутриента
    var proteinMode by remember { mutableStateOf(calorieGoal?.proteinMode ?: "percent") }
    var fatMode by remember { mutableStateOf(calorieGoal?.fatMode ?: "percent") }
    var carbsMode by remember { mutableStateOf(calorieGoal?.carbsMode ?: "percent") }

    // Текстовые значения для ввода (зависят от режима)
    var proteinValue by remember {
        mutableStateOf(
            when (proteinMode) {
                "grams" -> calorieGoal?.proteinGrams?.toString() ?: ""
                "grams_per_kg" -> calorieGoal?.proteinGramsPerKg?.toString() ?: ""
                else -> calorieGoal?.proteinPercent?.toString() ?: "30"
            }
        )
    }
    var fatValue by remember {
        mutableStateOf(
            when (fatMode) {
                "grams" -> calorieGoal?.fatGrams?.toString() ?: ""
                "grams_per_kg" -> calorieGoal?.fatGramsPerKg?.toString() ?: ""
                else -> calorieGoal?.fatPercent?.toString() ?: "30"
            }
        )
    }
    var carbsValue by remember {
        mutableStateOf(
            when (carbsMode) {
                "grams" -> calorieGoal?.carbsGrams?.toString() ?: ""
                "grams_per_kg" -> calorieGoal?.carbsGramsPerKg?.toString() ?: ""
                else -> calorieGoal?.carbsPercent?.toString() ?: "40"
            }
        )
    }

    // Пересчитанная цель (для предпросмотра)
    val weightKg = userProfile?.weight ?: 0.0
    val recalculated = remember(dailyCalories, proteinMode, fatMode, carbsMode, proteinValue, fatValue, carbsValue, weightKg) {
        calorieGoalViewModel.recalculateGoal(
            dailyCalories = dailyCalories.toIntOrNull() ?: 0,
            weightKg = weightKg,
            proteinMode = proteinMode, fatMode = fatMode, carbsMode = carbsMode,
            proteinValue = proteinValue, fatValue = fatValue, carbsValue = carbsValue
        )
    }

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
            // Рекомендация из профиля
            if (calculatedCalories > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Рекомендовано на основе профиля:", style = MaterialTheme.typography.labelMedium)
                        Text("$calculatedCalories ккал/день", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = {
                            dailyCalories = calculatedCalories.toString()
                        }) { Text("Использовать рекомендацию") }
                    }
                }
            }

            // Калории
            OutlinedTextField(
                value = dailyCalories,
                onValueChange = { dailyCalories = it.filter { c -> c.isDigit() } },
                label = { Text("Калории в день") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("ккал") },
                modifier = Modifier.fillMaxWidth()
            )

            // БЖУ с переключателями режимов
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Распределение БЖУ", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                        // Белки
                    MacroNutrientRow(
                        label = "Белки",
                        mode = proteinMode,
                        onModeChange = { proteinMode = it },
                        value = proteinValue,
                        onValueChange = { proteinValue = it },
                        calories = dailyCalories.toIntOrNull() ?: 0,
                        grams = recalculated.proteinGrams,
                        gramsPerKg = recalculated.proteinGramsPerKg,
                        percent = recalculated.proteinPercent,
                        weightKg = weightKg
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Жиры
                    MacroNutrientRow(
                        label = "Жиры",
                        mode = fatMode,
                        onModeChange = { fatMode = it },
                        value = fatValue,
                        onValueChange = { fatValue = it },
                        calories = dailyCalories.toIntOrNull() ?: 0,
                        grams = recalculated.fatGrams,
                        gramsPerKg = recalculated.fatGramsPerKg,
                        percent = recalculated.fatPercent,
                        weightKg = weightKg
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Углеводы
                    MacroNutrientRow(
                        label = "Углеводы",
                        mode = carbsMode,
                        onModeChange = { carbsMode = it },
                        value = carbsValue,
                        onValueChange = { carbsValue = it },
                        calories = dailyCalories.toIntOrNull() ?: 0,
                        grams = recalculated.carbsGrams,
                        gramsPerKg = recalculated.carbsGramsPerKg,
                        percent = recalculated.carbsPercent,
                        weightKg = weightKg
                    )
                }
            }

            // Предпросмотр
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Предварительный просмотр", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Белки: ${recalculated.proteinGrams} г (${recalculated.proteinPercent}%)")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Жиры: ${recalculated.fatGrams} г (${recalculated.fatPercent}%)")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Углеводы: ${recalculated.carbsGrams} г (${recalculated.carbsPercent}%)")
                    }
                    val calFromMacros = recalculated.proteinGrams * 4 + recalculated.fatGrams * 9 + recalculated.carbsGrams * 4
                    Text("Калорий из БЖУ: $calFromMacros ккал", style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = {
                    calorieGoalViewModel.saveCalorieGoal(recalculated)
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = dailyCalories.toIntOrNull()?.let { it > 0 } == true && !isLoading
            ) {
                Text("Сохранить цели")
            }
        }
    }
}

@Composable
fun MacroNutrientRow(
    label: String,
    mode: String,
    onModeChange: (String) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    calories: Int,
    grams: Int,
    gramsPerKg: Double,
    percent: Int,
    weightKg: Double
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, modifier = Modifier.weight(1f))
            // Переключатель режимов
            Row {
                FilterChip(
                    selected = mode == "percent",
                    onClick = { onModeChange("percent") },
                    label = { Text("%") },
                    border = BorderStroke(1.dp, if (mode == "percent") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(Modifier.width(4.dp))
                FilterChip(
                    selected = mode == "grams",
                    onClick = { onModeChange("grams") },
                    label = { Text("г") },
                    border = BorderStroke(1.dp, if (mode == "grams") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)

                )
                Spacer(Modifier.width(4.dp))
                FilterChip(
                    selected = mode == "grams_per_kg",
                    onClick = { onModeChange("grams_per_kg") },
                    label = { Text("г/кг") },
                    border = BorderStroke(1.dp, if (mode == "grams_per_kg") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() || c == '.' }) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(when (mode) {
                "grams" -> "г"
                "grams_per_kg" -> "г/кг"
                else -> "%"
            }) }
        )
        // Отображение эквивалентов в других единицах
        Text(
            text = when (mode) {
                "grams" -> "≈ $percent% • ${"%.1f".format(gramsPerKg)} г/кг"
                "grams_per_kg" -> "≈ ${grams} г • $percent%"
                else -> "≈ ${grams} г • ${"%.1f".format(gramsPerKg)} г/кг"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}