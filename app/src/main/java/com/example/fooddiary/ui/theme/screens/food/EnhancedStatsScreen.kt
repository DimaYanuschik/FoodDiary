package com.example.fooddiary.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.ui.components.charts.*
import com.example.fooddiary.ui.viewmodels.CalorieGoalViewModel
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedStatsScreen(
    onNavigateBack: () -> Unit
) {
//    val foodViewModel: FoodViewModel = viewModel()
    val foodViewModel: FoodViewModel = hiltViewModel()

    val calorieGoalViewModel: CalorieGoalViewModel = viewModel()

    val dailyStats by foodViewModel.dailyStats.collectAsState()
    val weeklyStats by foodViewModel.weeklyStats.collectAsState()
    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()

    // Преобразуем данные для графиков
    val weeklyChartData = remember(weeklyStats) {
        weeklyStats.map { it.date to it.totalCalories }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Детальная статистика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Заголовок с датой
                HeaderCard()

                // Основные метрики калорий
                if (calorieGoal != null) {
                    CalorieMetricsSection(
                        dailyStats = dailyStats,
                        calorieGoal = calorieGoal!!
                    )
                }

                // Распределение БЖУ
                MacroDistributionSection(
                    dailyStats = dailyStats,
                    calorieGoal = calorieGoal
                )

                // Недельный прогресс
                if (weeklyChartData.isNotEmpty() && calorieGoal != null) {
                    WeeklyProgressSection(
                        weeklyData = weeklyChartData,
                        goal = calorieGoal!!.dailyCalories
                    )
                }

                // Итоговая статистика
                SummarySection(
                    dailyStats = dailyStats,
                    calorieGoal = calorieGoal
                )

                // Советы
                AdviceSection(
                    dailyStats = dailyStats,
                    calorieGoal = calorieGoal
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Сегодня",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        .format(Date()),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(
                Icons.Filled.CalendarToday,
                contentDescription = "Календарь",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CalorieMetricsSection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Прогресс по калориям",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val caloriesProgress = dailyStats.totalCalories.toFloat() / calorieGoal.dailyCalories

                CircularProgressChart(
                    progress = caloriesProgress.coerceIn(0f, 1f),
                    label = "Калории",
                    currentValue = dailyStats.totalCalories,
                    targetValue = calorieGoal.dailyCalories,
                    color = if (caloriesProgress > 1) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.weight(1f)
                )

                CircularProgressChart(
                    progress = (dailyStats.totalProtein.toFloat() / calorieGoal.proteinGrams).coerceIn(0f, 1f),
                    label = "Белки",
                    currentValue = dailyStats.totalProtein.toInt(),
                    targetValue = calorieGoal.proteinGrams,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )

                CircularProgressChart(
                    progress = (dailyStats.totalFat.toFloat() / calorieGoal.fatGrams).coerceIn(0f, 1f),
                    label = "Жиры",
                    currentValue = dailyStats.totalFat.toInt(),
                    targetValue = calorieGoal.fatGrams,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun MacroDistributionSection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Распределение макронутриентов",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            calorieGoal?.let { goal ->
                MacroDistributionChart(
                    protein = dailyStats.totalProtein.toFloat(),
                    fat = dailyStats.totalFat.toFloat(),
                    carbs = dailyStats.totalCarbs.toFloat(),
                    proteinGoal = goal.proteinGrams,
                    fatGoal = goal.fatGrams,
                    carbsGoal = goal.carbsGrams,
                    modifier = Modifier.fillMaxWidth()
                )
            } ?: run {
                // Если цель не установлена, показываем только текущее распределение
                Text(
                    text = "Установите цели для отображения прогресса",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun WeeklyProgressSection(
    weeklyData: List<Pair<Date, Int>>,
    goal: Int
) {
    WeeklyProgressChart(
        dailyData = weeklyData,
        goal = goal,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SummarySection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Итоги за день",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            calorieGoal?.let { goal ->
                val remainingCalories = goal.dailyCalories - dailyStats.totalCalories
                val remainingProtein = goal.proteinGrams - dailyStats.totalProtein
                val remainingFat = goal.fatGrams - dailyStats.totalFat
                val remainingCarbs = goal.carbsGrams - dailyStats.totalCarbs

                SummaryItem(
                    label = "Осталось калорий",
                    value = if (remainingCalories > 0) "+$remainingCalories" else remainingCalories.toString(),
                    isPositive = remainingCalories > 0
                )

                SummaryItem(
                    label = "Осталось белков",
                    value = if (remainingProtein > 0) "+$remainingProtein" else remainingProtein.toString(),
                    unit = "г",
                    isPositive = remainingProtein > 0
                )

                SummaryItem(
                    label = "Осталось жиров",
                    value = if (remainingFat > 0) "+$remainingFat" else remainingFat.toString(),
                    unit = "г",
                    isPositive = remainingFat > 0
                )

                SummaryItem(
                    label = "Осталось углеводов",
                    value = if (remainingCarbs > 0) "+$remainingCarbs" else remainingCarbs.toString(),
                    unit = "г",
                    isPositive = remainingCarbs > 0
                )
            } ?: run {
                SummaryItem(
                    label = "Всего калорий",
                    value = dailyStats.totalCalories.toString(),
                    unit = "ккал"
                )

                SummaryItem(
                    label = "Белки",
                    value = dailyStats.totalProtein.toString(),
                    unit = "г"
                )

                SummaryItem(
                    label = "Жиры",
                    value = dailyStats.totalFat.toString(),
                    unit = "г"
                )

                SummaryItem(
                    label = "Углеводы",
                    value = dailyStats.totalCarbs.toString(),
                    unit = "г"
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    unit: String = "ккал",
    isPositive: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyMedium,
            color = if (value.startsWith("+")) {
                MaterialTheme.colorScheme.primary
            } else if (value.startsWith("-")) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun AdviceSection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = "Совет",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Советы и рекомендации",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            calorieGoal?.let { goal ->
                val remainingCalories = goal.dailyCalories - dailyStats.totalCalories

                val advice = when {
                    remainingCalories > 500 -> "Отличный прогресс! У вас еще много калорий в запасе. Можно позволить себе полезный перекус."
                    remainingCalories > 0 -> "Вы на правильном пути! Осталось $remainingCalories ккал до цели."
                    remainingCalories == 0 -> "Поздравляем! Вы достигли дневной цели по калориям!"
                    remainingCalories > -200 -> "Вы немного превысили цель. Это нормально! Завтра можно скорректировать питание."
                    else -> "Сегодня было калорийное меню. Не расстраивайтесь! Завтра новый день для достижения целей."
                }

                Text(
                    text = advice,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Дополнительные советы по макронутриентам
                val remainingProtein = goal.proteinGrams - dailyStats.totalProtein
                if (remainingProtein > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💪 Рекомендуем добавить белковые продукты: курицу, рыбу, творог.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (dailyStats.totalFat > goal.fatGrams * 1.2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Обратите внимание на потребление жиров. Попробуйте выбрать менее жирные варианты продуктов.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } ?: run {
                Text(
                    text = "Установите цели по калориям для получения персональных рекомендаций!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}