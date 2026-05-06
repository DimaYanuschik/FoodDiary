package com.example.fooddiary.ui.screens.food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    onNavigateBack: () -> Unit,
    onFoodAdded: () -> Unit
) {
//    val viewModel: FoodViewModel = viewModel()
    val viewModel: FoodViewModel = hiltViewModel()

    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Завтрак") }
    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")

    var isExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавить блюдо") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !isLoading
                    ) {
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
                .padding(16.dp),
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

            // Тип приема пищи
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                OutlinedTextField(
                    value = mealType,
                    onValueChange = {},
                    label = { Text("Прием пищи") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    mealTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                mealType = type
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            // Калории
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it.filter { char -> char.isDigit() } },
                label = { Text("Калории (ккал)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.LocalFireDepartment, contentDescription = null) }
            )

            // БЖУ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Белки
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Белки") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    suffix = { Text("г") }
                )

                // Жиры
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Жиры") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(
                            Icons.Default.OilBarrel,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    suffix = { Text("г") }
                )

                // Углеводы
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Углеводы") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Grain,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    suffix = { Text("г") }
                )
            }

            // Расчетные калории из БЖУ
            val calculatedCalories = remember(protein, fat, carbs) {
                val p = protein.toDoubleOrNull() ?: 0.0
                val f = fat.toDoubleOrNull() ?: 0.0
                val c = carbs.toDoubleOrNull() ?: 0.0
                (p * 4 + f * 9 + c * 4).toInt()
            }

            if (protein.isNotEmpty() || fat.isNotEmpty() || carbs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Расчетные калории:")
                        Text(
                            text = "$calculatedCalories ккал",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка добавления
            Button(
                onClick = {
                    val foodEntry = ScannedFoodEntry(
                        name = foodName,
                        calories = calories.toIntOrNull() ?: calculatedCalories,
                        protein = protein.toDoubleOrNull() ?: 0.0,
                        fat = fat.toDoubleOrNull() ?: 0.0,
                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                        date = Date(),
                        mealType = mealType,
                        //Костыль
                        notes = "",
                        barcode = null,
                        source = "barcode_scan",
                        originalProduct = null
                    )

                    viewModel.addFoodEntry(foodEntry)
                    onFoodAdded()
                    onNavigateBack()
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить блюдо")
                }
            }
        }
    }
}