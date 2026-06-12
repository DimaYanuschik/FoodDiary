package com.example.fooddiary.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.data_old.models.*
import com.example.fooddiary.ui.components.DatePicker
import com.example.fooddiary.ui.theme.WireframeTheme
import com.example.fooddiary.ui.viewmodels.UserProfileViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit,
//    viewModel: UserProfileViewModel = hiltViewModel()
    sharedViewModelStoreOwner: ViewModelStoreOwner? = null,
    // Для разделения настройки профиля после регистрации и просто редактирования
    viewModel: UserProfileViewModel = if (sharedViewModelStoreOwner != null) {
        hiltViewModel(sharedViewModelStoreOwner)
    } else  {
        hiltViewModel()
    }
//    userId: String
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val calculatedCalories by viewModel.calculatedCalories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

//    LaunchedEffect(Unit) {
//        viewModel.setUserId(userId)
//        viewModel.resetSaveSuccess()
//    }

    LaunchedEffect(Unit) {
        viewModel.resetSaveSuccess()
    }

    // Состояния для формы
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: Gender.MALE) }
    var birthDate by remember { mutableStateOf(userProfile?.birthDate ?: getDefaultBirthDate()) }
    var weight by remember { mutableStateOf(userProfile?.weight?.toString() ?: "") }
    var height by remember { mutableStateOf(userProfile?.height?.toString() ?: "") }
    var activityLevel by remember { mutableStateOf(userProfile?.activityLevel ?: ActivityLevel.MODERATE) }
    var goal by remember { mutableStateOf(userProfile?.goal ?: Goal.MAINTAIN) }

    // Состояния для выпадающих меню
    var genderExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }

    // Состояние для диалога выбора даты
    var showDatePicker by remember { mutableStateOf(false) }

    // Вычисляем возраст
    val age = remember(birthDate) {
        calculateAge(birthDate)
    }

    // Реактивно вычисляем калории при изменении параметров
    val reactiveCalories = remember(name, gender, birthDate, weight, height, activityLevel, goal) {
        val weightValue = weight.toDoubleOrNull() ?: 0.0
        val heightValue = height.toIntOrNull() ?: 0

        if (weightValue > 0 && heightValue > 0) {
            viewModel.calculateCaloriesForCurrentState(
                name = name,
                gender = gender,
                birthDate = birthDate,
                weight = weightValue,
                height = heightValue,
                activityLevel = activityLevel,
                goal = goal
            )
        } else {
            0
        }
    }

    val isFormValid = remember(name, weight, height) {
        name.isNotBlank() &&
                weight.isNotBlank() && weight.toDoubleOrNull() != null && weight.toDouble() > 0 &&
                height.isNotBlank() && height.toIntOrNull() != null && height.toInt() > 0
    }

    // Костыль TODO: исправить
//    LaunchedEffect(userProfile) {
//        userProfile?.let { profile ->
//            name = profile.name
//            gender = profile.gender
//            birthDate = profile.birthDate
//            weight = profile.weight.toString()
//            height = profile.height.toString()
//            activityLevel = profile.activityLevel
//            goal = profile.goal
//        }
//    }


    if (showDatePicker) {
        DatePicker(
            initialDate = birthDate,
            onDateSelected = { newDate ->
                birthDate = newDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Настройка профиля") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (reactiveCalories > 0) {
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
                                    text = "Рекомендуемая норма:",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = "$reactiveCalories ккал/день",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "изменения отразятся после сохранения",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    val saveSuccess by viewModel.saveSuccess.collectAsState()


                    LaunchedEffect(saveSuccess) {
                        if (saveSuccess) {
                            viewModel.resetSaveSuccess()
                            onComplete()
                        }
                    }
                    Button(
                        onClick = {
                            val profile = UserProfile(
                                id = userProfile?.id ?: "", // Сохраняем существующий ID если есть
                                name = name,
                                gender = gender,
                                birthDate = birthDate,
                                weight = weight.toDouble(),
                                height = height.toInt(),
                                activityLevel = activityLevel,
                                goal = goal,
                                createdAt = userProfile?.createdAt ?: Date(),
                                updatedAt = Date()
                            )
                            viewModel.saveUserProfile(profile)
                            // onComplete()
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
                            Text("Сохранить профиль")
                        }
                    }
                }
            }
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
//            Text(text = "${viewModel.currentUserId}")
            // Имя
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null)
                },
                singleLine = true
            )

            // Пол
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it }
            ) {
                OutlinedTextField(
                    value = gender.toString(),
                    onValueChange = {},
                    label = { Text("Пол") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Face, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.values().forEach { genderOption ->
                        DropdownMenuItem(
                            text = { Text(genderOption.toString()) },
                            onClick = {
                                gender = genderOption
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            // Дата рождения
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Дата рождения",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))


                    Button(
                        onClick = {
                            // Устанавливаем состояние, чтобы показать диалог
                            showDatePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Cake,
                                contentDescription = "Дата рождения",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Дата рождения: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(birthDate)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Возраст: $age лет",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Вес
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Вес") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.MonitorWeight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    suffix = { Text("кг") }
                )

                // Рост
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { char -> char.isDigit() } },
                    label = { Text("Рост") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Height,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    suffix = { Text("см") }
                )
            }

            // Уровень активности
            ExposedDropdownMenuBox(
                expanded = activityExpanded,
                onExpandedChange = { activityExpanded = it }
            ) {
                OutlinedTextField(
                    value = activityLevel.description,
                    onValueChange = {},
                    label = { Text("Уровень активности") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Filled.DirectionsRun, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = activityExpanded,
                    onDismissRequest = { activityExpanded = false }
                ) {
                    ActivityLevel.values().forEach { activity ->
                        DropdownMenuItem(
                            text = { Text(activity.description) },
                            onClick = {
                                activityLevel = activity
                                activityExpanded = false
                            }
                        )
                    }
                }
            }

            // Цель
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = it }
            ) {
                OutlinedTextField(
                    value = goal.description,
                    onValueChange = {},
                    label = { Text("Цель") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Flag, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    Goal.values().forEach { goalOption ->
                        DropdownMenuItem(
                            text = { Text(goalOption.description) },
                            onClick = {
                                goal = goalOption
                                goalExpanded = false
                            }
                        )
                    }
                }
            }

            // Информационная карточка
            if (weight.isNotBlank() && height.isNotBlank()) {
                val weightValue = weight.toDoubleOrNull() ?: 0.0
                val heightValue = height.toIntOrNull() ?: 0

                if (weightValue > 0 && heightValue > 0) {
                    val bmi = weightValue / ((heightValue / 100.0) * (heightValue / 100.0))

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Индекс массы тела (ИМТ):",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = String.format("%.1f", bmi),
                                style = MaterialTheme.typography.headlineMedium,
                                color = getBmiColor(bmi)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = getBmiCategory(bmi),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun getDefaultBirthDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, -30)
    return calendar.time
}

private fun calculateAge(birthDate: Date): Int {
    val today = Calendar.getInstance()
    val birthCalendar = Calendar.getInstance().apply { time = birthDate }

    var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    // Проверяем, был ли уже день рождения в этом году
    val todayMonth = today.get(Calendar.MONTH)
    val birthMonth = birthCalendar.get(Calendar.MONTH)
    if (todayMonth < birthMonth ||
        (todayMonth == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
        age--
    }

    return max(age, 0)
}

@Composable
private fun DatePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Выберите дату рождения",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -30)
                        }
                        onDateSelected(calendar.time)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("30 лет назад")
                }

                Button(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -25)
                        }
                        onDateSelected(calendar.time)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("25 лет назад")
                }

                Button(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -20)
                        }
                        onDateSelected(calendar.time)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("20 лет назад")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Отмена")
                }
            }
        }
    }
}

private fun getBmiColor(bmi: Double): androidx.compose.ui.graphics.Color {
    return when {
        bmi < 18.5 -> androidx.compose.ui.graphics.Color(0xFF2196F3) // Синий - недостаток
        bmi < 25 -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Зеленый - норма
        bmi < 30 -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Оранжевый - избыток
        else -> androidx.compose.ui.graphics.Color(0xFFF44336) // Красный - ожирение
    }
}

private fun getBmiCategory(bmi: Double): String {
    return when {
        bmi < 16 -> "Выраженный дефицит массы"
        bmi < 18.5 -> "Недостаточная масса"
        bmi < 25 -> "Нормальная масса"
        bmi < 30 -> "Избыточная масса"
        bmi < 35 -> "Ожирение 1 степени"
        bmi < 40 -> "Ожирение 2 степени"
        else -> "Ожирение 3 степени"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserProfileScreenPreview_Wireframe() {
    WireframeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Настройка профиля", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Surface(tonalElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Рекомендуемая норма:", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                                Box(modifier = Modifier.width(100.dp).height(24.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("изменения отразятся после сохранения", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color.DarkGray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Сохранить профиль", color = Color.White)
                        }
                    }
                }
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
                // Поле Имя
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                // Поле Пол
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Пол") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        readOnly = true,
                        leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }

                // Дата рождения
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Дата рождения", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Дата рождения: ", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                                        Box(modifier = Modifier.width(80.dp).height(16.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Возраст: ", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                                        Box(modifier = Modifier.width(40.dp).height(14.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                        Text(" лет", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }

//                // Вес и рост
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    OutlinedTextField(
//                        value = "",
//                        onValueChange = {},
//                        label = { Text("Вес") },
//                        modifier = Modifier.weight(1f),
//                        leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
//                        trailingIcon = { Text("кг") },
//                        suffix = { Text("кг") },
//                        singleLine = true,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.Black,
//                            unfocusedTextColor = Color.Black,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.LightGray
//                        )
//                    )
//                    OutlinedTextField(
//                        value = "",
//                        onValueChange = {},
//                        label = { Text("Рост") },
//                        modifier = Modifier.weight(1f),
//                        leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
//                        trailingIcon = { Text("см") },
//                        suffix = { Text("см") },
//                        singleLine = true,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.Black,
//                            unfocusedTextColor = Color.Black,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.LightGray
//                        )
//                    )
//                }
//
//                // Уровень активности
//                ExposedDropdownMenuBox(
//                    expanded = false,
//                    onExpandedChange = {}
//                ) {
//                    OutlinedTextField(
//                        value = "",
//                        onValueChange = {},
//                        label = { Text("Уровень активности") },
//                        modifier = Modifier.fillMaxWidth().menuAnchor(),
//                        readOnly = true,
//                        leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.Black,
//                            unfocusedTextColor = Color.Black,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.LightGray
//                        )
//                    )
//                }
//
//                // Цель
//                ExposedDropdownMenuBox(
//                    expanded = false,
//                    onExpandedChange = {}
//                ) {
//                    OutlinedTextField(
//                        value = "",
//                        onValueChange = {},
//                        label = { Text("Цель") },
//                        modifier = Modifier.fillMaxWidth().menuAnchor(),
//                        readOnly = true,
//                        leadingIcon = { Box(modifier = Modifier.size(20.dp).background(Color.DarkGray, RoundedCornerShape(4.dp))) },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.Black,
//                            unfocusedTextColor = Color.Black,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.LightGray
//                        )
//                    )
//                }

                // ИМТ
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Индекс массы тела (ИМТ):", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                        Box(modifier = Modifier.width(60.dp).height(28.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.width(140.dp).height(14.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                    }
                }
            }
        }
    }
}