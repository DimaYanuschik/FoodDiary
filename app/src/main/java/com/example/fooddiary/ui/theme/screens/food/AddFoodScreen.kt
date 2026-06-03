package com.example.fooddiary.ui.screens.food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.util.*

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddFoodScreen(
//    onNavigateBack: () -> Unit,
//    onFoodAdded: () -> Unit,
//
//    sharedViewModelStoreOwner: ViewModelStoreOwner,
//    viewModel: FoodViewModel = hiltViewModel(sharedViewModelStoreOwner)
////    userId: String
//) {
//
////    LaunchedEffect(userId) {
////        viewModel.setUserId(userId)
////    }
//
//    var foodName by remember { mutableStateOf("") }
//    var calories by remember { mutableStateOf("") }
//    var protein by remember { mutableStateOf("") }
//    var fat by remember { mutableStateOf("") }
//    var carbs by remember { mutableStateOf("") }
//    var mealType by remember { mutableStateOf("Завтрак") }
//    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")
//
//    var isExpanded by remember { mutableStateOf(false) }
//
//    val isLoading by viewModel.isLoading.collectAsState()
//    val selectedDate by viewModel.selectedDate.collectAsState()
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Добавить блюдо") },
//                navigationIcon = {
//                    IconButton(
//                        onClick = onNavigateBack,
//                        enabled = !isLoading
//                    ) {
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
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Название блюда
//            OutlinedTextField(
//                value = foodName,
//                onValueChange = { foodName = it },
//                label = { Text("Название блюда") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true,
//                leadingIcon = { Icon(Icons.Filled.Restaurant, contentDescription = null) }
//            )
//
//            // Тип приема пищи
//            ExposedDropdownMenuBox(
//                expanded = isExpanded,
//                onExpandedChange = { isExpanded = it }
//            ) {
//                OutlinedTextField(
//                    value = mealType,
//                    onValueChange = {},
//                    label = { Text("Прием пищи") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .menuAnchor(),
//                    readOnly = true,
//                    leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = null) },
//                    trailingIcon = {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
//                    }
//                )
//                ExposedDropdownMenu(
//                    expanded = isExpanded,
//                    onDismissRequest = { isExpanded = false }
//                ) {
//                    mealTypes.forEach { type ->
//                        DropdownMenuItem(
//                            text = { Text(type) },
//                            onClick = {
//                                mealType = type
//                                isExpanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//            // Калории
//            OutlinedTextField(
//                value = calories,
//                onValueChange = { calories = it.filter { char -> char.isDigit() } },
//                label = { Text("Калории (ккал)") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                leadingIcon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = null) }
//            )
//
//            // БЖУ
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // Белки
//                OutlinedTextField(
//                    value = protein,
//                    onValueChange = { protein = it.filter { char -> char.isDigit() || char == '.' } },
//                    label = { Text("Белки") },
//                    modifier = Modifier.weight(1f),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.FitnessCenter,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    },
//                    suffix = { Text("г") }
//                )
//
//                // Жиры
//                OutlinedTextField(
//                    value = fat,
//                    onValueChange = { fat = it.filter { char -> char.isDigit() || char == '.' } },
//                    label = { Text("Жиры") },
//                    modifier = Modifier.weight(1f),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    leadingIcon = {
//                        Icon(
//                            Icons.Default.OilBarrel,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    },
//                    suffix = { Text("г") }
//                )
//
//                // Углеводы
//                OutlinedTextField(
//                    value = carbs,
//                    onValueChange = { carbs = it.filter { char -> char.isDigit() || char == '.' } },
//                    label = { Text("Углеводы") },
//                    modifier = Modifier.weight(1f),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    leadingIcon = {
//                        Icon(
//                            Icons.Default.Grain,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    },
//                    suffix = { Text("г") }
//                )
//            }
//
//            // Расчетные калории из БЖУ
//            val calculatedCalories = remember(protein, fat, carbs) {
//                val p = protein.toDoubleOrNull() ?: 0.0
//                val f = fat.toDoubleOrNull() ?: 0.0
//                val c = carbs.toDoubleOrNull() ?: 0.0
//                (p * 4 + f * 9 + c * 4).toInt()
//            }
//
//            if (protein.isNotEmpty() || fat.isNotEmpty() || carbs.isNotEmpty()) {
//                Card(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("Расчетные калории:")
//                        Text(
//                            text = "$calculatedCalories ккал",
//                            color = MaterialTheme.colorScheme.primary,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // Кнопка добавления
//            Button(
//                onClick = {
//                    val foodEntry = ScannedFoodEntry(
//                        name = foodName,
//                        calories = calories.toIntOrNull() ?: calculatedCalories,
//                        protein = protein.toDoubleOrNull() ?: 0.0,
//                        fat = fat.toDoubleOrNull() ?: 0.0,
//                        carbs = carbs.toDoubleOrNull() ?: 0.0,
////                        date = Date(),
//                        date = selectedDate,
//                        mealType = mealType,
//                        //Костыль
//                        notes = "",
//                        barcode = null,
//                        source = "barcode_scan",
//                        originalProduct = null
//                    )
//
//                    viewModel.addFoodEntry(foodEntry)
//                    onFoodAdded()
////                    onNavigateBack()
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = foodName.isNotBlank() && !isLoading
//            ) {
//                if (isLoading) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(16.dp),
//                        strokeWidth = 2.dp
//                    )
//                } else {
//                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Добавить блюдо")
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    onNavigateBack: () -> Unit,
    onFoodAdded: () -> Unit,
    sharedViewModelStoreOwner: ViewModelStoreOwner? = null
) {
    val viewModel: FoodViewModel = if (sharedViewModelStoreOwner != null) {
        hiltViewModel(sharedViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    // Поля ввода
    var foodName by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Перекус") }
    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")
    var isMealExpanded by remember { mutableStateOf(false) }

    // Состав на 100 г
    var proteinPer100 by remember { mutableStateOf("") }
    var fatPer100 by remember { mutableStateOf("") }
    var carbsPer100 by remember { mutableStateOf("") }

    // Порция
    var portionWeight by remember { mutableStateOf("100") } // вес одной порции в граммах
    var portionCount by remember { mutableStateOf("1") }    // количество порций

    // Итоговое значение калорий (редактируемое)
    var totalCalories by remember { mutableStateOf("") }

    // Функция пересчёта абсолютных КБЖУ и калорий
    fun recalculate() {
        val prot = proteinPer100.toDoubleOrNull() ?: 0.0
        val fat = fatPer100.toDoubleOrNull() ?: 0.0
        val carb = carbsPer100.toDoubleOrNull() ?: 0.0
        val weight = portionWeight.toDoubleOrNull() ?: 100.0
        val count = portionCount.toDoubleOrNull() ?: 1.0

        val factor = (weight / 100.0) * count
        val totalProtein = prot * factor
        val totalFat = fat * factor
        val totalCarbs = carb * factor
        val calculatedCalories = (totalProtein * 4 + totalFat * 9 + totalCarbs * 4).toInt()

        // Автоматически обновляем поле калорий только если пользователь его не редактировал вручную
        // Проверим, не было ли ручного изменения (сравним с предыдущим автоматическим значением)
//        if (totalCalories.isBlank() || totalCalories.toIntOrNull() == calculatedCalories ||
//            totalCalories.toIntOrNull() == 0) {
//            totalCalories = calculatedCalories.toString()
//        }
        totalCalories = calculatedCalories.toString()
    }

    // Пересчитываем при изменении любого из исходных полей
    LaunchedEffect(proteinPer100, fatPer100, carbsPer100, portionWeight, portionCount) {
        recalculate()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавить блюдо") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isLoading) {
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
            // Название блюда
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("Название блюда") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Restaurant, contentDescription = null) }
            )

            // Тип приёма пищи
            ExposedDropdownMenuBox(
                expanded = isMealExpanded,
                onExpandedChange = { isMealExpanded = it }
            ) {
                OutlinedTextField(
                    value = mealType,
                    onValueChange = {},
                    label = { Text("Приём пищи") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMealExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = isMealExpanded,
                    onDismissRequest = { isMealExpanded = false }
                ) {
                    mealTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                mealType = type
                                isMealExpanded = false
                            }
                        )
                    }
                }
            }

            // Состав на 100 г
            Text("Состав на 100 г", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = proteinPer100,
                    onValueChange = { proteinPer100 = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Белки") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = fatPer100,
                    onValueChange = { fatPer100 = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Жиры") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = carbsPer100,
                    onValueChange = { carbsPer100 = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Углеводы") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("г") }
                )
            }

            // Вес порции и количество
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = portionWeight,
                    onValueChange = { portionWeight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Вес порции") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = portionCount,
                    onValueChange = { portionCount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Кол-во порций") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("шт") }
                )
            }

            // Итоговые калории (редактируемое)
            OutlinedTextField(
                value = totalCalories,
                onValueChange = { totalCalories = it.filter { c -> c.isDigit() } },
                label = { Text("Калории") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = null) },
                suffix = { Text("ккал") }
            )

            // Предпросмотр итоговых БЖУ (нередактируемые)
            val prot100 = proteinPer100.toDoubleOrNull() ?: 0.0
            val fat100 = fatPer100.toDoubleOrNull() ?: 0.0
            val carb100 = carbsPer100.toDoubleOrNull() ?: 0.0
            val w = portionWeight.toDoubleOrNull() ?: 100.0
            val c = portionCount.toDoubleOrNull() ?: 1.0
            val factor = (w / 100.0) * c
            val totalProtein = prot100 * factor
            val totalFat = fat100 * factor
            val totalCarbs = carb100 * factor

            if (foodName.isNotBlank() || proteinPer100.isNotEmpty() || fatPer100.isNotEmpty() || carbsPer100.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Итоговые значения", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Белки:")
                            Text("%.1f г".format(totalProtein))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Жиры:")
                            Text("%.1f г".format(totalFat))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Углеводы:")
                            Text("%.1f г".format(totalCarbs))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Кнопка добавления
            Button(
                onClick = {
                    val entry = ScannedFoodEntry(
                        id = UUID.randomUUID().toString(),
                        name = foodName,
                        calories = totalCalories.toIntOrNull() ?: 0,
                        protein = totalProtein,
                        fat = totalFat,
                        carbs = totalCarbs,
                        date = selectedDate,
                        mealType = mealType,
                        notes = "",
                        barcode = null,
                        source = "manual",
                        originalProduct = null
                    )
                    viewModel.addFoodEntry(entry)
                    onFoodAdded()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = foodName.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить блюдо")
                }
            }
        }
    }
}