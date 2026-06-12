package com.example.fooddiary.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data_old.auth.AuthRepository
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.data_old.repository.DailyStats
import com.example.fooddiary.data_old.repository.FoodEntry
import com.example.fooddiary.presentation.screens.home.HomeViewModel
import com.example.fooddiary.presentation.screens.water.WaterViewModel
import com.example.fooddiary.ui.components.CalendarStrip
import com.example.fooddiary.ui.components.WaterTrackerCard
import com.example.fooddiary.ui.screens.food.FoodItemCard
import com.example.fooddiary.ui.viewmodels.CalorieGoalViewModel
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import com.example.fooddiary.ui.viewmodels.UserProfileViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.example.fooddiary.data_old.models.CalorieGoal
import com.example.fooddiary.presentation.screens.water.WaterUiState
import com.example.fooddiary.ui.theme.WireframeTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToBarcodeScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val userId by homeViewModel.userId.collectAsStateWithLifecycle()
    val isLoadingUser by homeViewModel.isLoading.collectAsStateWithLifecycle()

//  Старая навигация с флагами

//    var showCameraScreen by remember { mutableStateOf(false) }
//    var showGalleryScreen by remember { mutableStateOf(false) }
//    var showAddFoodScreen by remember { mutableStateOf(false) }
//    var showStatsScreen by remember { mutableStateOf(false) }
//    var showProfileScreen by remember { mutableStateOf(false) }
//    var showGoalsScreen by remember { mutableStateOf(false) }
//    var showBarcodeScanner by remember { mutableStateOf(false) }
//
//    var barcodeResult by remember { mutableStateOf<BarcodeScanResult?>(null) }
//
//
//
//    // Определяем, какой экран показывать
//    when {
//        showCameraScreen -> {
//            CameraScreen(
//                onPhotoTaken = { uri ->
//                    println("Фото сделано: $uri")
//                    showCameraScreen = false
//                    showAddFoodScreen = true
//                },
//                onNavigateBack = { showCameraScreen = false }
//            )
//        }
//
//        showGalleryScreen -> {
//            GalleryPickerScreen(
//                onImageSelected = { uri ->
//                    println("Фото выбрано: $uri")
//                    showGalleryScreen = false
//                    showAddFoodScreen = true
//                },
//                onNavigateBack = { showGalleryScreen = false }
//            )
//        }
//
//        showAddFoodScreen -> {
//            AddFoodScreen(
//                onNavigateBack = { showAddFoodScreen = false },
//                onFoodAdded = { showAddFoodScreen = false }
//            )
//        }
//
//        showStatsScreen -> {
//            EnhancedStatsScreen(
//                onNavigateBack = { showStatsScreen = false }
//            )
//        }
//
//        showProfileScreen -> {
//            if(userId.isNotEmpty()) {
//                UserProfileScreen(
//                    onComplete = { showProfileScreen = false },
//                    onNavigateBack = { showProfileScreen = false },
//                    userId = userId
//                )
//            } else {
//                showProfileScreen = false
//            }
//        }
//
//        showGoalsScreen -> {
//            CalorieGoalScreen(
//                onComplete = { showGoalsScreen = false },
//                onNavigateBack = { showGoalsScreen = false }
//            )
//        }
//
//        showBarcodeScanner -> {
//            BarcodeScannerScreen(
//                onProductFound = { result ->
//                    barcodeResult = result
//                    showBarcodeScanner = false
//                },
//                onNavigateBack = { showBarcodeScanner = false }
//            )
//        }
//
//        barcodeResult != null -> {
//            BarcodeProductScreen(
//                scanResult = barcodeResult!!,
//                onAddToDiary = {
//                    // После добавления в дневник
//                    barcodeResult = null
//                },
//                onNavigateBack = { barcodeResult = null }
//            )
//        }
//
//
//        else -> {
//            if (!isLoadingUser) {
//                MainHomeScreen(
//                    onLogout = onLogout,
//                    onOpenCamera = { showCameraScreen = true },
//                    onOpenGallery = { showGalleryScreen = true },
//                    onOpenAddFood = { showAddFoodScreen = true },
//                    onOpenStats = { showStatsScreen = true },
//                    onOpenProfile = { showProfileScreen = true },
//                    onOpenGoals = { showGoalsScreen = true },
//                    onOpenBarcodeScanner = { showBarcodeScanner = true },
//                    userId = userId
//                )
//            } else {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//        }
//    }

    if (isLoadingUser) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        MainHomeScreen(
//            userId = userId,
            onLogout = onLogout,
            onOpenCamera = onNavigateToCamera,
            onOpenGallery = onNavigateToGallery,
            onOpenAddFood = onNavigateToAddFood,
            onOpenStats = onNavigateToStats,
            onOpenProfile = onNavigateToProfile,
            onOpenGoals = onNavigateToGoals,
            onOpenBarcodeScanner = onNavigateToBarcodeScanner,
            onOpenSearch = onNavigateToSearch
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
//    userId: String,
    onLogout: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAddFood: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenBarcodeScanner: () -> Unit,
    onOpenSearch: () -> Unit
) {

    val foodViewModel: FoodViewModel = hiltViewModel()
    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val calorieGoalViewModel: CalorieGoalViewModel = hiltViewModel()

    val waterViewModel: WaterViewModel = hiltViewModel()
    val waterUiState by waterViewModel.uiState.collectAsState() // TODO: возможно лучше вынести в WaterTrackerCard


//    LaunchedEffect(userId) {
//        if (userId.isNotEmpty()) {
//            foodViewModel.setUserId(userId)
//            userProfileViewModel.setUserId(userId)
//            calorieGoalViewModel.setUserId(userId)
//        }
//    }

    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val calorieGoal by calorieGoalViewModel.calorieGoal.collectAsState()
    val dailyStats by foodViewModel.dailyStats.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()

    val selectedDate by foodViewModel.selectedDate.collectAsState()
    var showFullCalendar by remember { mutableStateOf(false) }
    val selectedDateEntries by foodViewModel.selectedDateEntries.collectAsState()


    // Для синхронизации даты с модулем Water
    LaunchedEffect(selectedDate) {
        waterViewModel.loadForDate(selectedDate)
    }

    // Состояние для шторки
    var showAddSheet by remember { mutableStateOf(false) }

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

        CalendarStrip(
            selectedDate = selectedDate,
            onDateSelected = { date -> foodViewModel.selectDate(date) },
            onCalendarClick = { showFullCalendar = true },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Основной контент с прокруткой
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//            // Заголовок
//            Text(
//                text = "Трекер питания и калорий",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(top = 8.dp)
//            )

//            // Кнопки действий в сетке
//            Column(
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    HomeActionButton(
//                        icon = Icons.Filled.Camera,
//                        text = "Сфотографировать",
//                        onClick = onOpenCamera,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    HomeActionButton(
//                        icon = Icons.Filled.PhotoLibrary,
//                        text = "Из галереи",
//                        onClick = onOpenGallery,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    HomeActionButton(
//                        icon = Icons.Filled.QrCodeScanner,
//                        text = "Сканировать штрихкод",
//                        onClick = onOpenBarcodeScanner,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    HomeActionButton(
//                        icon = Icons.Filled.Add,
//                        text = "Добавить вручную",
//                        onClick = onOpenAddFood,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    HomeActionButton(
//                        icon = Icons.Filled.BarChart,
//                        text = "Статистика",
//                        onClick = {
//                            foodViewModel.refreshData()
//                            onOpenStats()
//                        },
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    HomeActionButton(
//                        icon = Icons.Filled.Search,
//                        text = "Поиск",
//                        onClick = onOpenSearch,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }

            // Круговой прогресс-бар калорий
            CalorieProgressCard(
                dailyStats = dailyStats,
                calorieGoal = calorieGoal,
                onSettingsClick = onOpenGoals,
                modifier = Modifier.fillMaxWidth()
            )

            FoodListSection(
                onAddFoodClick = onOpenAddFood,
                modifier = Modifier.fillMaxWidth(),
                entries = selectedDateEntries,
                stats = dailyStats,
                isLoading = isLoading,
                onDelete = { id -> foodViewModel.deleteFoodEntry(id) }
            )

            WaterTrackerCard(
                uiState = waterUiState,
                onAddWater = { amount -> waterViewModel.addWater(amount) },
                onDeleteEntry = { id -> waterViewModel.deleteEntry(id) },
                onSetGoal = { goal -> waterViewModel.setGoal(goal) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Нижняя навигационная панель
        BottomNavigationBar(
            onOpenProfile = onOpenProfile,
            onOpenGoals = onOpenGoals,
            onLogout = onLogout,
            onOpenStats = onOpenStats,
            onAddClick = { showAddSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

//    // Кнопка «+» в центре нижней части экрана
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .wrapContentSize(Alignment.BottomCenter)
//            .padding(bottom = 16.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        FloatingActionButton(
//            onClick = { showAddSheet = true },
//            containerColor = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.size(56.dp)
//        ) {
//            Icon(Icons.Filled.Add, contentDescription = "Добавить запись")
//        }
//    }

    // Шторка добавления
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 8.dp
        ) {
            AddFoodBottomSheet(
                onCameraClick = {
                    showAddSheet = false
                    onOpenCamera()
                },
                onGalleryClick = {
                    showAddSheet = false
                    onOpenGallery()
                },
                onBarcodeClick = {
                    showAddSheet = false
                    onOpenBarcodeScanner()
                },
                onManualAddClick = {
                    showAddSheet = false
                    onOpenAddFood()
                },
                onSearchClick = {
                    showAddSheet = false
                    onOpenSearch()
                }
            )
        }
    }

    if (showFullCalendar) {
        FullCalendarDialog(
            currentDate = selectedDate,
            onDateSelected = { date ->
                foodViewModel.selectDate(date)
                showFullCalendar = false
            },
            onDismiss = { showFullCalendar = false }
        )
    }
}

@Composable
fun AddFoodBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onBarcodeClick: () -> Unit,
    onManualAddClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Добавить запись",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        // Верхний ряд — 2 кнопки
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeActionButton(
                icon = Icons.Filled.CameraAlt,
                text = "Сделать фото",
                onClick = onCameraClick,
                modifier = Modifier.size(100.dp)
            )
            HomeActionButton(
                icon = Icons.Filled.PhotoLibrary,
                text = "Из галереи",
                onClick = onGalleryClick,
                modifier = Modifier.size(100.dp)
            )
        }
        // Нижний ряд — 3 кнопки
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeActionButton(
                icon = Icons.Filled.QrCodeScanner,
                text = "Штрих-код",
                onClick = onBarcodeClick,
                modifier = Modifier.size(100.dp)
            )
            HomeActionButton(
                icon = Icons.Filled.Add,
                text = "Вручную",
                onClick = onManualAddClick,
                modifier = Modifier.size(100.dp)
            )
            HomeActionButton(
                icon = Icons.Filled.Search,
                text = "Поиск",
                onClick = onSearchClick,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCalendarDialog(
    currentDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    // Простейшая реализация: используем DatePickerDialog из Material3
    // Но можно сделать свой календарь на целый месяц.
    // Воспользуемся DatePickerDialog, требующим Android DatePicker.
    // Для Compose используем DatePickerDialog из compose-material3
    // или AndroidView.
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentDate.time)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onDateSelected(Date(millis))
                } ?: onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun FoodListSection(
    onAddFoodClick: () -> Unit,
    modifier: Modifier = Modifier,

    entries: List<FoodEntry>,
    stats: DailyStats,
    isLoading: Boolean,
    onDelete: (String) -> Unit
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
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
//            // Внутренняя прокрутка для списка еды
//            FoodListScreenWithScroll(
//                onAddFoodClick = onAddFoodClick,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(16.dp)
//            )
            FoodListContent(
                entries = entries,
                stats = stats,
                isLoading = isLoading,
                onAddFoodClick = onAddFoodClick,
                onDelete = onDelete
            )
        }
    }
}

//@Composable
//fun FoodListContent(
//    entries: List<FoodEntry>,
//    stats: DailyStats,
//    isLoading: Boolean,
//    onAddFoodClick: () -> Unit,
//    onDelete: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
////    val viewModel: FoodViewModel = hiltViewModel()
////    val todayFoodEntries by viewModel.todayFoodEntries.collectAsState()
////    val dailyStats by viewModel.dailyStats.collectAsState()
////    val isLoading by viewModel.isLoading.collectAsState()
//
//    Column(
//        modifier = modifier.verticalScroll(rememberScrollState())
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Сегодняшние приемы пищи",
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            IconButton(
//                onClick = onAddFoodClick,
//                modifier = Modifier.size(36.dp)
//            ) {
//                Icon(Icons.Filled.Add, contentDescription = "Добавить")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Статистика за день
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Text(
//                        text = "Всего за день",
//                        style = MaterialTheme.typography.labelMedium
//                    )
//                    Text(
//                        text = "${stats.totalCalories} ккал",
//                        style = MaterialTheme.typography.titleLarge,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                Column(
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = "БЖУ",
//                        style = MaterialTheme.typography.labelMedium
//                    )
//                    Text(
//                        text = "${String.format("%.1f", stats.totalProtein)} / " +
//                                "${String.format("%.1f", stats.totalFat)} / " +
//                                "${String.format("%.1f", stats.totalCarbs)} г",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else if (entries.isEmpty()) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = onAddFoodClick
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Icon(
//                        Icons.Filled.Restaurant,
//                        contentDescription = null,
//                        modifier = Modifier.size(48.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Text(
//                        text = "Добавьте первый прием пищи",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//
//                    Text(
//                        text = "Нажмите чтобы добавить",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        } else {
//            // Список еды по приемам пищи
//            val meals = entries.groupBy { it.mealType }
//
//            Column(
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                meals.forEach { (mealType, entries) ->
//                    Text(
//                        text = mealType,
//                        style = MaterialTheme.typography.titleSmall,
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.padding(horizontal = 8.dp)
//                    )
//
//                    entries.forEach { food ->
//                        FoodItemCard(
//                            food = food,
//                            onDelete = { onDelete(food.id) }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun FoodListContent(
    entries: List<FoodEntry>,
    stats: DailyStats,
    isLoading: Boolean,
    onAddFoodClick: () -> Unit,
    onDelete: (String) -> Unit,
    newlyAddedMealType: String? = null,   // <-- новый параметр
    modifier: Modifier = Modifier
) {
    val expandedMealTypes = remember { mutableStateMapOf<String, Boolean>() }

    // Автоматически раскрываем категорию, в которую только что добавили продукт
    LaunchedEffect(newlyAddedMealType) {
        if (newlyAddedMealType != null) {
            expandedMealTypes.keys.forEach { key -> expandedMealTypes[key] = false }
            expandedMealTypes[newlyAddedMealType] = true
        }
    }

    Column(modifier = modifier) {
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (entries.isEmpty()) {
            Card(
                onClick = onAddFoodClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Добавьте первый приём пищи")
                    Text("Нажмите, чтобы добавить", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            val meals = entries.groupBy { it.mealType }

//            // Состояния для каждой категории (по умолчанию все закрыты)
//            val expandedMealTypes = remember { mutableStateMapOf<String, Boolean>() }

            meals.forEach { (mealType, mealEntries) ->
                val isExpanded = expandedMealTypes.getOrPut(mealType) { false }

                // Суммарные показатели для этой категории
                val catCalories = mealEntries.sumOf { it.calories }
                val catProtein = mealEntries.sumOf { it.protein }
                val catFat = mealEntries.sumOf { it.fat }
                val catCarbs = mealEntries.sumOf { it.carbs }

                // Иконка приёма пищи
                val icon = when (mealType) {
                    "Завтрак" -> Icons.Filled.FreeBreakfast
                    "Обед" -> Icons.Filled.LunchDining
                    "Ужин" -> Icons.Filled.DinnerDining
                    else -> Icons.Filled.Cookie   // для "Перекус" и всего остального
                }

                Card(
                    onClick = { expandedMealTypes[mealType] = !isExpanded },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isExpanded) 4.dp else 2.dp
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = mealType,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "$mealType (${mealEntries.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "$catCalories ккал  •  Б: ${String.format("%.1f", catProtein)}  Ж: ${String.format("%.1f", catFat)}  У: ${String.format("%.1f", catCarbs)} г",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .padding(bottom = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                mealEntries.forEach { food ->
                                    FoodItemCard(food = food, onDelete = { onDelete(food.id) })
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// Версия до календаря
@Composable
fun FoodListScreenWithScroll(
    onAddFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FoodViewModel = hiltViewModel()
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
    onAddClick: () -> Unit,
    onOpenStats: () -> Unit,
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
                .height(64.dp)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
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

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Filled.AddCircle,
                    contentDescription = "Добавить запись",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            NavigationButton(
                icon = Icons.Filled.BarChart,
                text = "Статистика",
                onClick = onOpenStats
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
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
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

