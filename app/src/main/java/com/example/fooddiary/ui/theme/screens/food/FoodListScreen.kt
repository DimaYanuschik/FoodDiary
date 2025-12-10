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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data.repository.FoodEntry
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FoodListScreen(
    onAddFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FoodViewModel = viewModel()
    val foodEntries by viewModel.foodEntries.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = modifier) {
        // Заголовок и кнопка добавления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сегодняшние приемы пищи",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = onAddFoodClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Статистика за день
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Всего за день",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${dailyStats.totalCalories} ккал",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "БЖУ",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${String.format("%.1f", dailyStats.totalProtein)} / " +
                                "${String.format("%.1f", dailyStats.totalFat)} / " +
                                "${String.format("%.1f", dailyStats.totalCarbs)} г",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (foodEntries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddFoodClick
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Добавьте первый прием пищи",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Нажмите чтобы добавить",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            // Список еды по приемам пищи
            val meals = foodEntries.groupBy { it.mealType }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                meals.forEach { (mealType, entries) ->
                    item {
                        Text(
                            text = mealType,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    items(entries) { food ->
                        FoodItemCard(
                            food = food,
                            onDelete = { viewModel.deleteFoodEntry(food.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    food: FoodEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${food.calories} ккал • " +
                            "Б: ${String.format("%.1f", food.protein)}г " +
                            "Ж: ${String.format("%.1f", food.fat)}г " +
                            "У: ${String.format("%.1f", food.carbs)}г",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}