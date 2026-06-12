package com.example.fooddiary.ui.screens.food

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.theme.WireframeTheme
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.util.*

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
    var totalProtein by remember { mutableStateOf(0.0) }
    var totalFat by remember { mutableStateOf(0.0) }
    var totalCarbs by remember { mutableStateOf(0.0) }

    val searchProduct by viewModel.selectedSearchProduct.collectAsState()

    // Синхронный пересчёт всех итоговых значений
    fun updateTotals() {
        val prot = proteinPer100.toDoubleOrNull() ?: 0.0
        val fat = fatPer100.toDoubleOrNull() ?: 0.0
        val carb = carbsPer100.toDoubleOrNull() ?: 0.0
        val weight = portionWeight.toDoubleOrNull() ?: 100.0
        val count = portionCount.toDoubleOrNull() ?: 1.0

        Log.d("AddFood", "updateTotals $prot, $fat, $carb, $weight, $count")

        val factor = (weight / 100.0) * count
        totalProtein = prot * factor
        totalFat = fat * factor
        totalCarbs = carb * factor
        totalCalories = ((totalProtein * 4) + (totalFat * 9) + (totalCarbs * 4)).toInt().toString()
    }
//
//    // Функция пересчёта абсолютных КБЖУ и калорий
//    fun recalculate() {
//        val prot = proteinPer100.toDoubleOrNull() ?: 0.0
//        val fat = fatPer100.toDoubleOrNull() ?: 0.0
//        val carb = carbsPer100.toDoubleOrNull() ?: 0.0
//        val weight = portionWeight.toDoubleOrNull() ?: 100.0
//        val count = portionCount.toDoubleOrNull() ?: 1.0
//
//        val factor = (weight / 100.0) * count
//        val totalProtein = prot * factor
//        val totalFat = fat * factor
//        val totalCarbs = carb * factor
//        val calculatedCalories = (totalProtein * 4 + totalFat * 9 + totalCarbs * 4).toInt()
//
//        // Автоматически обновляем поле калорий только если пользователь его не редактировал вручную
//        // Проверим, не было ли ручного изменения (сравним с предыдущим автоматическим значением)
////        if (totalCalories.isBlank() || totalCalories.toIntOrNull() == calculatedCalories ||
////            totalCalories.toIntOrNull() == 0) {
////            totalCalories = calculatedCalories.toString()
////        }
//        totalCalories = calculatedCalories.toString()
//    }

// При получении продукта из поиска заполняем поля и сбрасываем состояние
    LaunchedEffect(searchProduct) {
        Log.d("AddFoodScreen", "LE(searchProduct")
        searchProduct?.let { product ->
            foodName = product.name
            proteinPer100 = product.proteinsPer100g?.let { String.format(Locale.US, "%.1f", it) } ?: ""
            fatPer100 = product.fatsPer100g?.let { String.format(Locale.US, "%.1f", it) } ?: ""
            carbsPer100 = product.carbsPer100g?.let { String.format(Locale.US, "%.1f", it) } ?: ""

            portionWeight = "100"
            portionCount = "1"

            // Рассчитываем итоговые значения
            val prot = product.proteinsPer100g ?: 0.0
            val fat = product.fatsPer100g ?: 0.0
            val carb = product.carbsPer100g ?: 0.0
            val weight = 100.0
            val count = 1.0
            val factor = (weight / 100.0) * count

            totalProtein = prot * factor
            totalFat = fat * factor
            totalCarbs = carb * factor

            // Если есть хоть какие-то БЖУ, пересчитываем калории по формуле, иначе берём из продукта
            if (prot > 0 || fat > 0 || carb > 0) {
                totalCalories = ((totalProtein * 4) + (totalFat * 9) + (totalCarbs * 4)).toInt().toString()
            } else {
                totalCalories = product.caloriesPer100g.toInt().toString()
            }

            viewModel.clearSearchProduct()
        }
    }


    // Пересчитываем при изменении любого из исходных полей
//    LaunchedEffect(proteinPer100, fatPer100, carbsPer100, portionWeight, portionCount) {
//        recalculate()
//    }

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
                    onValueChange = {
                        proteinPer100 = it.filter { c -> c.isDigit() || c == '.' }
                        updateTotals()
                                    },
                    label = { Text("Белки") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = fatPer100,
                    onValueChange = {
                        fatPer100 = it.filter { c -> c.isDigit() || c == '.' }
                        updateTotals()
                                    },
                    label = { Text("Жиры") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = carbsPer100,
                    onValueChange = {
                        carbsPer100 = it.filter { c -> c.isDigit() || c == '.' }
                        updateTotals()
                                    },
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
                    onValueChange = {
                        portionWeight = it.filter { c -> c.isDigit() || c == '.' }
                        updateTotals()
                                    },
                    label = { Text("Вес порции") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("г") }
                )
                OutlinedTextField(
                    value = portionCount,
                    onValueChange = {
                        portionCount = it.filter { c -> c.isDigit() || c == '.' }
                        updateTotals()
                                    },
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
//            val totalProtein = prot100 * factor
//            val totalFat = fat100 * factor
//            val totalCarbs = carb100 * factor
            totalProtein = prot100 * factor
            totalFat = fat100 * factor
            totalCarbs = carb100 * factor

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
//                    viewModel.clearSearchProduct()
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
                    Text("Добавить в дневник")
                }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddFoodScreenPreview_Wireframe() {
    WireframeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Добавить блюдо", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
                // Поле "Название блюда"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)))
                        Spacer(Modifier.width(12.dp))
                        Text("Название блюда", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    }
                }

                // Поле "Приём пищи"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(start = 12.dp, end = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)))
                            Spacer(Modifier.width(12.dp))
                            Text("Приём пищи", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        }
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.DarkGray)
                    }
                }

                // Состав на 100 г
                Text("Состав на 100 г", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Белки
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Белки", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            Text("г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                    // Жиры
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Жиры", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            Text("г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                    // Углеводы
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Углеводы", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            Text("г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                }

                // Вес порции и количество
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Вес порции
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Вес порции", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            Text("г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                    // Кол-во порций
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Кол-во порций", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                            Spacer(Modifier.weight(1f))
                            Text("шт", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        }
                    }
                }

                // Калории
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(start = 12.dp, end = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).background(Color.DarkGray, RoundedCornerShape(4.dp)))
                        Spacer(Modifier.width(12.dp))
                        Text("Калории", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        Spacer(Modifier.weight(1f))
                        Text("ккал", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                    }
                }

                // Карточка "Итоговые значения"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Итоговые значения", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Белки:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(50.dp).height(16.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Text(" г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Жиры:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(50.dp).height(16.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Text(" г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Углеводы:", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(50.dp).height(16.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Text(" г", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Кнопка "Добавить в дневник"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.DarkGray, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Добавить в дневник", color = Color.White)
                    }
                }
            }
        }
    }
}
