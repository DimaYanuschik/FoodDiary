package com.example.fooddiary.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data.auth.AuthRepository
import com.example.fooddiary.ui.screens.camera.CameraScreen
import com.example.fooddiary.ui.screens.camera.GalleryPickerScreen
import com.example.fooddiary.ui.screens.food.AddFoodScreen
import com.example.fooddiary.ui.screens.food.FoodListScreen
import com.example.fooddiary.ui.screens.food.StatsScreen
import com.example.fooddiary.ui.viewmodels.FoodViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    var showCameraScreen by remember { mutableStateOf(false) }
    var showGalleryScreen by remember { mutableStateOf(false) }
    var showAddFoodScreen by remember { mutableStateOf(false) }
    var showStatsScreen by remember { mutableStateOf(false) }

    // Определяем, какой экран показывать
    when {
        showCameraScreen -> {
            CameraScreen(
                onPhotoTaken = { uri ->
                    // Здесь будет обработка фото (День 3)
                    println("Фото сделано: $uri")
                    showCameraScreen = false
                    // После фото можно открыть экран добавления еды
                    showAddFoodScreen = true
                },
                onNavigateBack = { showCameraScreen = false }
            )
        }
        showGalleryScreen -> {
            GalleryPickerScreen(
                onImageSelected = { uri ->
                    // Здесь будет обработка фото из галереи (День 3)
                    println("Фото выбрано: $uri")
                    showGalleryScreen = false
                    // После выбора фото можно открыть экран добавления еды
                    showAddFoodScreen = true
                },
                onNavigateBack = { showGalleryScreen = false }
            )
        }
        showAddFoodScreen -> {
            AddFoodScreen(
                onNavigateBack = { showAddFoodScreen = false },
                onFoodAdded = { showAddFoodScreen = false }
            )
        }
        showStatsScreen -> {
            StatsScreen(
                onNavigateBack = { showStatsScreen = false }
            )
        }
        else -> {
            MainHomeScreen(
                onLogout = onLogout,
                onOpenCamera = { showCameraScreen = true },
                onOpenGallery = { showGalleryScreen = true },
                onOpenAddFood = { showAddFoodScreen = true },
                onOpenStats = { showStatsScreen = true }
            )
        }
    }
}

@Composable
fun MainHomeScreen(
    onLogout: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAddFood: () -> Unit,
    onOpenStats: () -> Unit
) {
    val foodViewModel: FoodViewModel = viewModel()
    val authRepository = AuthRepository()

    // Устанавливаем userId при загрузке
    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { userId ->
            foodViewModel.setUserId(userId)
            foodViewModel.loadData()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Food Diary",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Трекер питания и калорий",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Основные кнопки в сетке 2x2
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка камеры
                HomeActionButton(
                    icon = Icons.Filled.Camera,
                    text = "Сфотографировать",
                    onClick = onOpenCamera,
                    modifier = Modifier.weight(1f)
                )

                // Кнопка галереи
                HomeActionButton(
                    icon = Icons.Filled.PhotoLibrary,
                    text = "Из галереи",
                    onClick = onOpenGallery,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка добавления вручную
                HomeActionButton(
                    icon = Icons.Filled.Add,
                    text = "Добавить вручную",
                    onClick = onOpenAddFood,
                    modifier = Modifier.weight(1f)
                )

                // Кнопка статистики
                HomeActionButton(
                    icon = Icons.Filled.BarChart,
                    text = "Статистика",
                    onClick = {
                        foodViewModel.loadData()
                        onOpenStats
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Список сегодняшней еды
        FoodListScreen(
            onAddFoodClick = onOpenAddFood,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка выхода
        TextButton(onClick = onLogout) {
            Text("Выйти")
        }
    }
}

@Composable
fun HomeActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}