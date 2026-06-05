package com.example.fooddiary.presentation.screens.foodrecognition

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodRecognitionScreen(
    onNavigateBack: () -> Unit,
    onFoodAdded: () -> Unit,
    selectedImageBitmap: Bitmap,
    sharedViewModelStoreOwner: ViewModelStoreOwner,
    recognitionViewModel: FoodRecognitionViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel(sharedViewModelStoreOwner)
) {
    LaunchedEffect(selectedImageBitmap) {
        recognitionViewModel.onImageSelected(selectedImageBitmap)
    }

    val uiState by recognitionViewModel.uiState.collectAsState()
    val selectedDate by foodViewModel.selectedDate.collectAsState()
    var refineQuery by remember { mutableStateOf("") }
    var showRefineField by remember { mutableStateOf(false) }

    var mealType by remember { mutableStateOf("Перекус") }
    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")
    var isMealExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Распознавание блюда") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Фото
            Image(
                bitmap = selectedImageBitmap.asImageBitmap(),
                contentDescription = "Фото блюда",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                contentScale = ContentScale.Fit
            )

            // Загрузка
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Анализируем блюдо...", style = MaterialTheme.typography.bodyMedium)
            }

            // Ошибка
            uiState.error?.let { error ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ошибка", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Редактируемый результат
            uiState.editableResult?.let { editable ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Название (редактируемое)
                        OutlinedTextField(
                            value = editable.name,
                            onValueChange = { newName ->
                                recognitionViewModel.updateEditableResult(editable.copy(name = newName))
                            },
                            label = { Text("Название блюда") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Калории
                        OutlinedTextField(
                            value = editable.calories.toInt().toString(),
                            onValueChange = { cal ->
                                recognitionViewModel.updateEditableResult(editable.copy(calories = cal.toDoubleOrNull() ?: 0.0))
                            },
                            label = { Text("Калории (ккал)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // БЖУ
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editable.protein.toString(),
                                onValueChange = { recognitionViewModel.updateEditableResult(editable.copy(protein = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Белки (г)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editable.fat.toString(),
                                onValueChange = { recognitionViewModel.updateEditableResult(editable.copy(fat = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Жиры (г)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editable.carbs.toString(),
                                onValueChange = { recognitionViewModel.updateEditableResult(editable.copy(carbs = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Углеводы (г)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(16.dp))
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

                        // Уточнение через ИИ
                        if (showRefineField) {
                            OutlinedTextField(
                                value = refineQuery,
                                onValueChange = { refineQuery = it },
                                label = { Text("Уточнить") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    recognitionViewModel.refineWithText(refineQuery)
                                    refineQuery = ""
                                    showRefineField = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = refineQuery.isNotBlank()
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Уточнить у ИИ")
                            }
                        } else {
                            TextButton(onClick = { showRefineField = true }) {
                                Text("Уточнить через ИИ")
                            }
                        }


                        // Добавление в дневник
                        Button(
                            onClick = {
                                val result = editable
                                val entry = ScannedFoodEntry(
                                    name = result.name,
                                    calories = result.calories.toInt(),
                                    protein = result.protein,
                                    fat = result.fat,
                                    carbs = result.carbs,
                                    date = selectedDate,
//                                    mealType = "Перекус"
                                    mealType = mealType
                                )
                                foodViewModel.addFoodEntry(entry)
                                onFoodAdded()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Добавить в дневник")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}