package com.example.fooddiary.ui.screens.barcode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.viewmodels.BarcodeScanState
import com.example.fooddiary.ui.viewmodels.BarcodeViewModel
import com.example.fooddiary.ui.viewmodels.FoodViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeProductScreen(
//    scanResult: BarcodeScanResult,
    barcode: String,
    onAddToDiary: () -> Unit,
    onNavigateBack: () -> Unit,
    sharedViewModelStoreOwner: ViewModelStoreOwner? = null
) {
    val barcodeViewModel: BarcodeViewModel = hiltViewModel()
//    val foodViewModel: FoodViewModel = hiltViewModel()
// Используем общую FoodViewModel, если она передана
    val foodViewModel: FoodViewModel = if (sharedViewModelStoreOwner != null) {
        hiltViewModel(sharedViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

//    // Загружаем продукт при старте
//    LaunchedEffect(barcode) {
//        barcodeViewModel.scanBarcodeFromBitmap(null) // сбросим состояние
//        // BarcodeViewModel умеет загружать продукт по штрихкоду?
//        // У нас есть fetchProductInfo, но он требует barcode и возвращает результат сразу.
//        // В ViewModel нет готового метода для загрузки по штрихкоду без bitmap.
//        // Придётся вызвать barcodeRepository.fetchProductInfo напрямую?
//        // Лучше добавить в BarcodeViewModel метод loadProductByBarcode(barcode)
//    }

    LaunchedEffect(barcode) {
        barcodeViewModel.fetchProductByBarcode(barcode)
    }

    val uiState by barcodeViewModel.uiState.collectAsState()
    val product = (uiState.scanState as? BarcodeScanState.ProductFound)?.result?.product

//    val product = scanResult.product

//    if (product == null) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text("Информация о продукте отсутствует")
//            Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
//                Text("Назад")
//            }
//        }
//        return
//    }

    // State для ввода данных
    var servingSize by remember { mutableStateOf("100") }
    var quantity by remember { mutableStateOf("1.0") } // Количество порций
    var notes by remember { mutableStateOf("") }

    // Вычисляем итоговые значения на основе ввода
    val quantityValue = quantity.toDoubleOrNull() ?: 1.0
    val servingSizeValue = servingSize.toDoubleOrNull() ?: 100.0

    // Коэффициент пересчета
    // Если servingSize = 100г и quantity = 1, то factor = 1.0
    val factor = (servingSizeValue / 100.0) * quantityValue

    if (uiState.scanState is BarcodeScanState.FetchingProduct || product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (uiState.scanState is BarcodeScanState.Error) {
        val errorMessage = (uiState.scanState as BarcodeScanState.Error).message
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ошибка: $errorMessage")
                Button(onClick = onNavigateBack) { Text("Назад") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Изображение продукта
            if (!product.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Фото продукта",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Основная информация
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.brand ?: "Бренд неизвестен",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // БЖУ Ряд
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NutritionItem("Ккал", ((product.calories * factor).toInt()).toString())
                        NutritionItem("Белки", String.format("%.1f", product.protein * factor))
                        NutritionItem("Жиры", String.format("%.1f", product.fat * factor))
                        NutritionItem("Углев.", String.format("%.1f", product.carbs * factor))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Поля ввода
            OutlinedTextField(
                value = quantity,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) quantity = it },
                label = { Text("Количество порций") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = servingSize,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) servingSize = it },
                label = { Text("Размер порции (г/мл)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Запись для дневника
                    val entry = ScannedFoodEntry(
                        id = UUID.randomUUID().toString(),
                        name = product.name,
                        calories = (product.calories * factor).toInt(),
                        protein = product.protein * factor,
                        fat = product.fat * factor,
                        carbs = product.carbs * factor,
//                        date = Date(),
                        date = foodViewModel.selectedDate.value,
                        barcode = product.barcode,
                        originalProduct = product,
                        source = "scanner"
                    )

                    // Сохраняем через ViewModel
                    foodViewModel.addFoodEntry(entry)
                    barcodeViewModel.saveProduct(product)

                    onAddToDiary()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Добавить в дневник")
            }
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}