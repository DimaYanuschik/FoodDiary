package com.example.fooddiary.ui.screens.barcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.ui.theme.WireframeTheme
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

    var mealType by remember { mutableStateOf("Перекус") }
    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")
    var isMealExpanded by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(16.dp))
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
                        source = "scanner",
                        mealType = mealType
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


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BarcodeProductScreenPreview_Wireframe() {
    WireframeTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Название",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                // Изображение продукта (заглушка)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                }

                Spacer(Modifier.height(16.dp))

                // Карточка с основной информацией
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Бренд
                        Text(
                            text = "Бренд",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.DarkGray
                        )
                        Spacer(Modifier.height(4.dp))
                        // Название продукта
                        Text(
                            text = "Название",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(8.dp))

                        // БЖУ Ряд
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Ккал
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Spacer(Modifier.height(2.dp))
                                Text("Ккал", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                            // Белки
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Spacer(Modifier.height(2.dp))
                                Text("Белки", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                            // Жиры
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Spacer(Modifier.height(2.dp))
                                Text("Жиры", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                            // Углеводы
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.width(40.dp).height(20.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                                Spacer(Modifier.height(2.dp))
                                Text("Углев.", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Поле "Количество порций"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("Количество порций", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                }

                Spacer(Modifier.height(16.dp))

                // Поле "Размер порции (г/мл)"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("Размер порции (г/мл)", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                }

                Spacer(Modifier.height(16.dp))

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
                        Text("Приём пищи", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.DarkGray)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Кнопка "Добавить в дневник"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.DarkGray, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добавить в дневник", color = Color.White)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BarcodeScannerScreenPreview_Wireframe() {
    WireframeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Сканирование штрихкода", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.DarkGray,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Gray)
            ) {
                // Имитация видоискателя камеры
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Рамка сканера (зелёные уголки — оставляем цвет для узнаваемости)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val scannerWidth = size.width * 0.8f
                        val scannerHeight = scannerWidth * 0.5f
                        val scannerLeft = (size.width - scannerWidth) / 2
                        val scannerTop = (size.height - scannerHeight) / 2
                        val scannerRight = scannerLeft + scannerWidth
                        val scannerBottom = scannerTop + scannerHeight
                        val cornerLength = 40.dp.toPx()
                        val strokeWidth = 4.dp.toPx()

                        // Левый верхний угол
                        drawLine(Color.Green, Offset(scannerLeft, scannerTop), Offset(scannerLeft + cornerLength, scannerTop), strokeWidth)
                        drawLine(Color.Green, Offset(scannerLeft, scannerTop), Offset(scannerLeft, scannerTop + cornerLength), strokeWidth)
                        // Правый верхний угол
                        drawLine(Color.Green, Offset(scannerRight, scannerTop), Offset(scannerRight - cornerLength, scannerTop), strokeWidth)
                        drawLine(Color.Green, Offset(scannerRight, scannerTop), Offset(scannerRight, scannerTop + cornerLength), strokeWidth)
                        // Левый нижний угол
                        drawLine(Color.Green, Offset(scannerLeft, scannerBottom), Offset(scannerLeft + cornerLength, scannerBottom), strokeWidth)
                        drawLine(Color.Green, Offset(scannerLeft, scannerBottom), Offset(scannerLeft, scannerBottom - cornerLength), strokeWidth)
                        // Правый нижний угол
                        drawLine(Color.Green, Offset(scannerRight, scannerBottom), Offset(scannerRight - cornerLength, scannerBottom), strokeWidth)
                        drawLine(Color.Green, Offset(scannerRight, scannerBottom), Offset(scannerRight, scannerBottom - cornerLength), strokeWidth)

                        // Сканирующая линия
                        val scanLineY = scannerTop + scannerHeight * 0.5f
                        drawLine(
                            Color.Green.copy(alpha = 0.8f),
                            Offset(scannerLeft + 10.dp.toPx(), scanLineY),
                            Offset(scannerRight - 10.dp.toPx(), scanLineY),
                            2.dp.toPx()
                        )
                    }
                }

                // Текст-подсказка внизу
                Text(
                    text = "Наведите камеру на штрихкод продукта",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CameraScreenPreview_Wireframe() {
    WireframeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Сфотографируйте еду", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Видоискатель камеры (серый фон с перекрестием)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Серый фон видоискателя
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    )

//                    // Перекрестие для фокусировки
//                    Box(modifier = Modifier.size(60.dp)) {
//                        // Горизонтальная линия
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(2.dp)
//                                .background(Color.DarkGray)
//                                .align(Alignment.Center)
//                        )
//                        // Вертикальная линия
//                        Box(
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .width(2.dp)
//                                .background(Color.DarkGray)
//                                .align(Alignment.Center)
//                        )
//                    }
                }

                // Кнопки управления (вертикально в правом верхнем углу)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    // Кнопка переключения камеры
//                    FloatingActionButton(
//                        onClick = {},
//                        containerColor = Color.DarkGray,
//                        modifier = Modifier.size(44.dp)
//                    ) {
//                        Icon(Icons.Filled.Cameraswitch, contentDescription = "Переключить камеру", tint = Color.White)
//                    }

                    // Кнопка вспышки
                    FloatingActionButton(
                        onClick = {},
                        containerColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.FlashOff, contentDescription = "Вспышка", tint = Color.DarkGray)
                    }

                    // Кнопка "Сделать фото"
                    FloatingActionButton(
                        onClick = {},
                        containerColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Camera, contentDescription = "Сфотографировать", tint = Color.DarkGray)
                    }
                }
            }
        }
    }
}