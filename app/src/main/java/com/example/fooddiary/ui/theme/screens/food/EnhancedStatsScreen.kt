package com.example.fooddiary.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.fooddiary.ui.components.charts.MacroDistributionChart
import com.example.fooddiary.ui.viewmodels.CalorieGoalViewModel
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedStatsScreen(
    onNavigateBack: () -> Unit,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
) {
    val foodViewModel: FoodViewModel = hiltViewModel(sharedViewModelStoreOwner)
    val calorieGoalViewModel: CalorieGoalViewModel = hiltViewModel(sharedViewModelStoreOwner)

    val dailyStats by foodViewModel.dailyStats.collectAsState()
    val weeklyStats by foodViewModel.weeklyStats.collectAsState()
    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()
    val selectedDate by foodViewModel.selectedDate.collectAsState()

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderCard(selectedDate = selectedDate)

                if (calorieGoal != null) {
                    CalorieMetricsSection(dailyStats = dailyStats, calorieGoal = calorieGoal!!)
                }

//                MacroDistributionSection(dailyStats = dailyStats, calorieGoal = calorieGoal)

                if (weeklyStats.isNotEmpty() && calorieGoal != null) {
                    ColorLegend(modifier = Modifier.padding(bottom = 16.dp))
                    WeeklySummaryCard(weeklyStats = weeklyStats, calorieGoal = calorieGoal!!)
                    WeeklyProgressSection(weeklyStats = weeklyStats, calorieGoal = calorieGoal!!)
                }

                SummarySection(dailyStats = dailyStats, calorieGoal = calorieGoal)
                AdviceSection(dailyStats = dailyStats, calorieGoal = calorieGoal)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderCard(selectedDate: Date) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Статистика за",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(selectedDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(Icons.Filled.CalendarToday, contentDescription = "Календарь",
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
@Composable
private fun CalorieMetricsSection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Прогресс по калориям", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CompactCircularChart(
                    progress = (dailyStats.totalCalories.toFloat() / calorieGoal.dailyCalories).coerceIn(0f, 1f),
                    label = "Калории",
                    currentValue = dailyStats.totalCalories,
                    targetValue = calorieGoal.dailyCalories,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                CompactCircularChart(
                    progress = (dailyStats.totalProtein.toFloat() / calorieGoal.proteinGrams).coerceIn(0f, 1f),
                    label = "Белки",
                    currentValue = dailyStats.totalProtein.toInt(),
                    targetValue = calorieGoal.proteinGrams,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CompactCircularChart(
                    progress = (dailyStats.totalFat.toFloat() / calorieGoal.fatGrams).coerceIn(0f, 1f),
                    label = "Жиры",
                    currentValue = dailyStats.totalFat.toInt(),
                    targetValue = calorieGoal.fatGrams,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                CompactCircularChart(
                    progress = (dailyStats.totalCarbs.toFloat() / calorieGoal.carbsGrams).coerceIn(0f, 1f),
                    label = "Углеводы",
                    currentValue = dailyStats.totalCarbs.toInt(),
                    targetValue = calorieGoal.carbsGrams,
                    color = Color(0xFFFF9800),
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
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Распределение макронутриентов", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp))

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
                Text("Установите цели для отображения прогресса",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun WeeklySummaryCard(
    weeklyStats: List<com.example.fooddiary.data_old.repository.DailyStats>,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    if (weeklyStats.isEmpty() || calorieGoal == null) return
    val avgCalories = weeklyStats.map { it.totalCalories }.average()
    val avgProtein = weeklyStats.map { it.totalProtein }.average()
    val avgFat = weeklyStats.map { it.totalFat }.average()
    val avgCarbs = weeklyStats.map { it.totalCarbs }.average()

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Средние показатели за неделю",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            MacroSummaryRow(
                label = "Калории",
                current = avgCalories,
                goal = calorieGoal.dailyCalories,
                unit = "ккал",
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            MacroSummaryRow(
                label = "Белки",
                current = avgProtein,
                goal = calorieGoal.proteinGrams,
                unit = "г",
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            MacroSummaryRow(
                label = "Жиры",
                current = avgFat,
                goal = calorieGoal.fatGrams,
                unit = "г",
                color = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(8.dp))

            MacroSummaryRow(
                label = "Углеводы",
                current = avgCarbs,
                goal = calorieGoal.carbsGrams,
                unit = "г",
                color = Color(0xFFFF9800)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun MacroSummaryRow(
    label: String,
    current: Double,
    goal: Int,
    unit: String,
    color: Color
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0.0, 1.5) else 0.0
    val progressColor = when {
        progress <= 0.8 -> Color(0xFF4CAF50)
        progress <= 1.0 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LinearProgressIndicator(
            progress = progress.toFloat().coerceAtMost(1.0f),
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "${String.format(Locale.US, "%.1f", current)} / $goal $unit",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(90.dp)
        )
    }
}

@Composable
private fun WeeklyProgressSection(
    weeklyStats: List<com.example.fooddiary.data_old.repository.DailyStats>,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Прогресс за неделю", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            weeklyStats.forEach { dayStats ->
                val date = dayStats.date
                val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                val dayNumber = SimpleDateFormat("dd.MM", Locale.getDefault()).format(date)

                val calProgress = if (calorieGoal != null && calorieGoal.dailyCalories > 0)
                    (dayStats.totalCalories.toFloat() / calorieGoal.dailyCalories)
                else 0f

                val calColor = when {
                    calProgress <= 0.8f -> Color(0xFF4CAF50)
                    calProgress <= 1.0f -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.width(60.dp)) {
                            Text(dayName, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(dayNumber, style = MaterialTheme.typography.bodySmall)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = calProgress.coerceAtMost(1f),
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                    color = calColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "${dayStats.totalCalories}/${calorieGoal?.dailyCalories ?: 0}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(70.dp)
                                )
                            }
                            if (calorieGoal != null) {
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    MacroMiniIndicator(
                                        label = "Б",
                                        current = dayStats.totalProtein.toInt(),
                                        goal = calorieGoal.proteinGrams,
                                        color = Color(0xFF4CAF50)
                                    )
                                    MacroMiniIndicator(
                                        label = "Ж",
                                        current = dayStats.totalFat.toInt(),
                                        goal = calorieGoal.fatGrams,
                                        color = Color(0xFF2196F3)
                                    )
                                    MacroMiniIndicator(
                                        label = "У",
                                        current = dayStats.totalCarbs.toInt(),
                                        goal = calorieGoal.carbsGrams,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                            }
                        }
                    }
                    if (weeklyStats.last() != dayStats) {
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun MacroMiniIndicator(label: String, current: Int, goal: Int, color: Color) {
    val progress = if (goal > 0) (current.toFloat() / goal) else 0f
    val indicatorColor = when {
        progress <= 0.8f -> Color(0xFF4CAF50)      // зелёный — норма
        progress <= 1.0f -> Color(0xFFFFC107)      // жёлтый — близко к норме
        else -> Color(0xFFF44336)                  // красный — превышение
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(4.dp)
                .then(
                    Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceAtMost(1f))
                    .background(color = indicatorColor, shape = MaterialTheme.shapes.small)
            )
        }
    }
}

@Composable
private fun SummarySection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Итоги за день", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp))

            calorieGoal?.let { goal ->
                val remainingCalories = goal.dailyCalories - dailyStats.totalCalories
                val remainingProtein = goal.proteinGrams - dailyStats.totalProtein
                val remainingFat = goal.fatGrams - dailyStats.totalFat
                val remainingCarbs = goal.carbsGrams - dailyStats.totalCarbs

                SummaryItem(label = "Осталось калорий",
                    value = if (remainingCalories > 0) "+$remainingCalories" else remainingCalories.toString(),
                    isPositive = remainingCalories > 0)
                SummaryItem(label = "Осталось белков",
                    value = if (remainingProtein > 0) "+$remainingProtein" else remainingProtein.toString(),
                    unit = "г", isPositive = remainingProtein > 0)
                SummaryItem(label = "Осталось жиров",
                    value = if (remainingFat > 0) "+$remainingFat" else remainingFat.toString(),
                    unit = "г", isPositive = remainingFat > 0)
                SummaryItem(label = "Осталось углеводов",
                    value = if (remainingCarbs > 0) "+$remainingCarbs" else remainingCarbs.toString(),
                    unit = "г", isPositive = remainingCarbs > 0)
            } ?: run {
                SummaryItem(label = "Всего калорий", value = dailyStats.totalCalories.toString(), unit = "ккал")
                SummaryItem(label = "Белки", value = dailyStats.totalProtein.toString(), unit = "г")
                SummaryItem(label = "Жиры", value = dailyStats.totalFat.toString(), unit = "г")
                SummaryItem(label = "Углеводы", value = dailyStats.totalCarbs.toString(), unit = "г")
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyMedium,
            color = if (value.startsWith("+")) MaterialTheme.colorScheme.primary
            else if (value.startsWith("-")) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AdviceSection(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(Icons.Filled.Lightbulb, contentDescription = "Совет", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Советы и рекомендации", style = MaterialTheme.typography.titleMedium)
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
                Text(advice, style = MaterialTheme.typography.bodyMedium)
                val remainingProtein = goal.proteinGrams - dailyStats.totalProtein
                if (remainingProtein > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("💪 Рекомендуем добавить белковые продукты: курицу, рыбу, творог.", style = MaterialTheme.typography.bodySmall)
                }
                if (dailyStats.totalFat > goal.fatGrams * 1.2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚠️ Обратите внимание на потребление жиров. Попробуйте выбрать менее жирные варианты продуктов.", style = MaterialTheme.typography.bodySmall)
                }
            } ?: run {
                Text("Установите цели по калориям для получения персональных рекомендаций!", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun ColorLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFF4CAF50), label = "Ниже нормы")
            LegendItem(color = Color(0xFFFFC107), label = "Близко к норме")
            LegendItem(color = Color(0xFFF44336), label = "Превышение")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = MaterialTheme.shapes.extraSmall)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun CompactCircularChart(
    progress: Float,
    label: String,
    currentValue: Int,
    targetValue: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {

            // Получаем цвет ошибки до входа в Canvas
            val errorColor = MaterialTheme.colorScheme.error
            val arcColor = if (progress > 1f) errorColor else color

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Фоновый круг
                drawArc(
                    color = color.copy(alpha = 0.1f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
                // Прогресс
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceAtMost(1f),
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = "$currentValue/$targetValue",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
