package com.example.fooddiary.ui.screens.food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fooddiary.data_old.repository.DailyStats
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit
) {
//    val viewModel: FoodViewModel = viewModel()
    val viewModel: FoodViewModel = hiltViewModel()

    val dailyStats by viewModel.dailyStats.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Загружаем данные при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Статистика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                        }
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
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Сегодняшняя статистика
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Сегодня",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Калории
                                StatItem(
                                    icon = Icons.Filled.LocalFireDepartment,
                                    title = "Калории",
                                    value = "${dailyStats.totalCalories} ккал",
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // БЖУ
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatItem(
                                        icon = Icons.Filled.FitnessCenter,
                                        title = "Белки",
                                        value = "${String.format("%.1f", dailyStats.totalProtein)} г",
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    StatItem(
                                        icon = Icons.Filled.OilBarrel,
                                        title = "Жиры",
                                        value = "${String.format("%.1f", dailyStats.totalFat)} г",
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    StatItem(
                                        icon = Icons.Filled.Grain,
                                        title = "Углеводы",
                                        value = "${String.format("%.1f", dailyStats.totalCarbs)} г",
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Статистика за неделю
                    item {
                        Text(
                            text = "Последние 7 дней",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(weeklyStats) { stats ->
                        DailyStatCard(stats = stats)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DailyStatCard(stats: DailyStats) {
    val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormat.format(stats.date),
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${stats.totalCalories} ккал",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Б: ${String.format("%.1f", stats.totalProtein)}г " +
                            "Ж: ${String.format("%.1f", stats.totalFat)}г " +
                            "У: ${String.format("%.1f", stats.totalCarbs)}г",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}