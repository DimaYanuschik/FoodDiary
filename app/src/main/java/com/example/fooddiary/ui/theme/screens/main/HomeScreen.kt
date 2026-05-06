package com.example.fooddiary.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data_old.auth.AuthRepository
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.ui.screens.camera.CameraScreen
import com.example.fooddiary.ui.screens.camera.GalleryPickerScreen
import com.example.fooddiary.ui.screens.food.AddFoodScreen
import com.example.fooddiary.ui.screens.barcode.BarcodeProductScreen
import com.example.fooddiary.ui.screens.barcode.BarcodeScannerScreen
import com.example.fooddiary.ui.screens.profile.CalorieGoalScreen
import com.example.fooddiary.ui.screens.profile.UserProfileScreen
import com.example.fooddiary.ui.screens.stats.EnhancedStatsScreen
import com.example.fooddiary.ui.viewmodels.CalorieGoalViewModel
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import com.example.fooddiary.ui.viewmodels.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
) {
    var showCameraScreen by remember { mutableStateOf(false) }
    var showGalleryScreen by remember { mutableStateOf(false) }
    var showAddFoodScreen by remember { mutableStateOf(false) }
    var showStatsScreen by remember { mutableStateOf(false) }
    var showProfileScreen by remember { mutableStateOf(false) }
    var showGoalsScreen by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

    var barcodeResult by remember { mutableStateOf<BarcodeScanResult?>(null) }

    // Определяем, какой экран показывать
    when {
        showCameraScreen -> {
            CameraScreen(
                onPhotoTaken = { uri ->
                    println("Фото сделано: $uri")
                    showCameraScreen = false
                    showAddFoodScreen = true
                },
                onNavigateBack = { showCameraScreen = false }
            )
        }

        showGalleryScreen -> {
            GalleryPickerScreen(
                onImageSelected = { uri ->
                    println("Фото выбрано: $uri")
                    showGalleryScreen = false
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
            EnhancedStatsScreen(
                onNavigateBack = { showStatsScreen = false }
            )
        }

        showProfileScreen -> {
            UserProfileScreen(
                onComplete = { showProfileScreen = false },
                onNavigateBack = { showProfileScreen = false }
            )
        }

        showGoalsScreen -> {
            CalorieGoalScreen(
                onComplete = { showGoalsScreen = false },
                onNavigateBack = { showGoalsScreen = false }
            )
        }

        showBarcodeScanner -> {
            BarcodeScannerScreen(
                onProductFound = { result ->
                    barcodeResult = result
                    showBarcodeScanner = false
                },
                onNavigateBack = { showBarcodeScanner = false }
            )
        }

        barcodeResult != null -> {
            BarcodeProductScreen(
                scanResult = barcodeResult!!,
                onAddToDiary = {
                    // После добавления в дневник
                    barcodeResult = null
                },
                onNavigateBack = { barcodeResult = null }
            )
        }


        else -> {
            MainHomeScreen(
                onLogout = onLogout,
                onOpenCamera = { showCameraScreen = true },
                onOpenGallery = { showGalleryScreen = true },
                onOpenAddFood = { showAddFoodScreen = true },
                onOpenStats = { showStatsScreen = true },
                onOpenProfile = { showProfileScreen = true },
                onOpenGoals = { showGoalsScreen = true },
                onOpenBarcodeScanner = { showBarcodeScanner = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onLogout: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAddFood: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenBarcodeScanner: () -> Unit
) {
//    val foodViewModel: FoodViewModel = viewModel()
    val foodViewModel: FoodViewModel = hiltViewModel()

    val userProfileViewModel: UserProfileViewModel = viewModel()
    val calorieGoalViewModel: CalorieGoalViewModel = viewModel()
    val authRepository = AuthRepository()

    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { userId ->
            foodViewModel.setUserId(userId)
            userProfileViewModel.setUserId(userId)
            calorieGoalViewModel.setUserId(userId)
        }
    }

    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val dailyStats by foodViewModel.dailyStats.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Food Diary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                userProfile?.name?.takeIf { it.isNotBlank() }?.let { name ->
                    Text(
                        text = "Привет, $name!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onOpenProfile,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Настройки")
            }
        }

        // Основной контент с прокруткой
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Text(
                text = "Трекер питания и калорий",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Кнопки действий в сетке
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionButton(
                        icon = Icons.Filled.Camera,
                        text = "Сфотографировать",
                        onClick = onOpenCamera,
                        modifier = Modifier.weight(1f)
                    )

                    HomeActionButton(
                        icon = Icons.Filled.PhotoLibrary,
                        text = "Из галереи",
                        onClick = onOpenGallery,
                        modifier = Modifier.weight(1f)
                    )

                    HomeActionButton(
                        icon = Icons.Filled.QrCodeScanner,
                        text = "Сканировать штрихкод",
                        onClick = onOpenBarcodeScanner,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionButton(
                        icon = Icons.Filled.Add,
                        text = "Добавить вручную",
                        onClick = onOpenAddFood,
                        modifier = Modifier.weight(1f)
                    )

                    HomeActionButton(
                        icon = Icons.Filled.BarChart,
                        text = "Статистика",
                        onClick = {
                            foodViewModel.refreshData()
                            onOpenStats()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Круговой прогресс-бар калорий
            CalorieProgressCard(
                dailyStats = dailyStats,
                calorieGoal = calorieGoal,
                onSettingsClick = onOpenGoals,
                modifier = Modifier.fillMaxWidth()
            )

            FoodListSection(
                onAddFoodClick = onOpenAddFood,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Нижняя навигационная панель
        BottomNavigationBar(
            onOpenProfile = onOpenProfile,
            onOpenGoals = onOpenGoals,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun FoodListSection(
    onAddFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
        ) {
            // Внутренняя прокрутка для списка еды
            FoodListScreenWithScroll(
                onAddFoodClick = onAddFoodClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun FoodListScreenWithScroll(
    onAddFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FoodViewModel = viewModel()
    val todayFoodEntries by viewModel.todayFoodEntries.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
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
        } else if (todayFoodEntries.isEmpty()) {
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
            val meals = todayFoodEntries.groupBy { it.mealType }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                meals.forEach { (mealType, entries) ->
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    entries.forEach { food ->
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
    food: com.example.fooddiary.data_old.repository.FoodEntry,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalorieProgressCard(
    dailyStats: com.example.fooddiary.data_old.repository.DailyStats,
    calorieGoal: com.example.fooddiary.data_old.models.CalorieGoal?,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Прогресс за сегодня",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Filled.Tune,
                        contentDescription = "Настроить цели",
                        modifier = Modifier.size(18.dp),
                        tint = colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            calorieGoal?.let { goal ->
                val caloriesConsumed = dailyStats.totalCalories
                val caloriesRemaining = goal.dailyCalories - caloriesConsumed
                val progress = (caloriesConsumed.toFloat() / goal.dailyCalories).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Круговой прогресс
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Фон
                            drawArc(
                                color = colorScheme.onSurface.copy(alpha = 0.1f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 10f, cap = StrokeCap.Round)
                            )

                            // Прогресс
                            drawArc(
                                color = if (progress > 1f) {
                                    colorScheme.error
                                } else {
                                    colorScheme.primary
                                },
                                startAngle = -90f,
                                sweepAngle = 360f * progress.coerceAtMost(1f),
                                useCenter = false,
                                style = Stroke(width = 10f, cap = StrokeCap.Round)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (progress > 1f) {
                                        colorScheme.error
                                    } else {
                                        colorScheme.primary
                                    }
                                )
                            )
                            Text(
                                text = "$caloriesConsumed / ${goal.dailyCalories}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = if (caloriesRemaining > 0) "Осталось: $caloriesRemaining" else "Превышено на ${-caloriesRemaining}",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = if (caloriesRemaining > 0) Color(0xFF4CAF50) else colorScheme.error
                                )
                            )
                        }
                    }
                }

                // БЖУ
                if (dailyStats.totalCalories > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroItem(
                            label = "Белки",
                            value = "${dailyStats.totalProtein.toInt()}г",
                            goal = goal.proteinGrams,
                            color = Color(0xFF4CAF50)
                        )
                        MacroItem(
                            label = "Жиры",
                            value = "${dailyStats.totalFat.toInt()}г",
                            goal = goal.fatGrams,
                            color = Color(0xFF2196F3)
                        )
                        MacroItem(
                            label = "Углеводы",
                            value = "${dailyStats.totalCarbs.toInt()}г",
                            goal = goal.carbsGrams,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            } ?: run {
                // Если цель не установлена
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Цели по калориям не установлены",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primaryContainer
                        )
                    ) {
                        Text("Установить цели")
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroItem(
    label: String,
    value: String,
    goal: Int,
    color: Color
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = "из $goal г",
            style = TextStyle(
                fontSize = 9.sp,
                color = colorScheme.onSurfaceVariant
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomNavigationBar(
    onOpenProfile: () -> Unit,
    onOpenGoals: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationButton(
                icon = Icons.Filled.Person,
                text = "Профиль",
                onClick = onOpenProfile
            )

            NavigationButton(
                icon = Icons.Filled.TrackChanges,
                text = "Цели",
                onClick = onOpenGoals
            )

            NavigationButton(
                icon = Icons.Filled.ExitToApp,
                text = "Выйти",
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun NavigationButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp),
            tint = colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
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
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}