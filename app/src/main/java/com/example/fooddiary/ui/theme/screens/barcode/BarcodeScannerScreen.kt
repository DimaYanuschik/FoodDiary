package com.example.fooddiary.ui.screens.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.ui.viewmodels.BarcodeScanState
import com.example.fooddiary.ui.viewmodels.BarcodeViewModel
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerScreen(
    onProductFound: (BarcodeScanResult) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: BarcodeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var lastAnalysisTime by remember { mutableStateOf(0L) }
    val analysisInterval = 1000L // 1 секунда между анализами

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        viewModel.setCameraAvailable(isGranted)
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
            viewModel.setCameraAvailable(true)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(uiState.scanState) {
        when (uiState.scanState) {
            is BarcodeScanState.ProductFound -> {
                val result = (uiState.scanState as BarcodeScanState.ProductFound).result
                onProductFound(result)
                viewModel.clearError()
            }
            is BarcodeScanState.Error -> {
                println("Ошибка сканирования: ${(uiState.scanState as BarcodeScanState.Error).message}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Сканирование штрихкода",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
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
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                CameraPreviewWithAnalysis(
                    modifier = Modifier.fillMaxSize(),
                    onFrameAvailable = { bitmap ->
                        val currentTime = System.currentTimeMillis()
                        // Ограничиваем частоту анализа
                        if (currentTime - lastAnalysisTime > analysisInterval &&
                            !uiState.isScanning &&
                            uiState.scanState is BarcodeScanState.Idle) {
                            lastAnalysisTime = currentTime
                            viewModel.autoScanBarcode(bitmap)
                        }
                    }
                )

                // Добавляем оверлей с рамкой сканера
                BarcodeScannerOverlay(
                    modifier = Modifier.fillMaxSize(),
                    isScanning = uiState.isScanning
                )

                // Показываем состояние сканирования
                when (val state = uiState.scanState) {
                    is BarcodeScanState.FetchingProduct -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 100.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.Green,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Ищем продукт ${state.barcode}...",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    is BarcodeScanState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 100.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Surface(
                                color = Color.Red.copy(alpha = 0.8f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ошибка",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.message,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.clearError() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Red
                                        )
                                    ) {
                                        Text("Попробовать снова")
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Камера",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Требуется доступ к камере",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Разрешите доступ к камере для сканирования штрихкодов",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Разрешить доступ к камере")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewWithAnalysis(
    modifier: Modifier = Modifier,
    onFrameAvailable: (android.graphics.Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
            ProcessCameraProvider.getInstance(context).also { future ->
                future.addListener({ continuation.resume(future.get()) }, ContextCompat.getMainExecutor(context))
            }
        }

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(1280, 720))
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            // Используем ImageProxy
            try {
                // Используем функцию из CameraUtils
                val bitmap = imageProxy.toBitmap()
                onFrameAvailable(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}