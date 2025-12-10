package com.example.fooddiary.ui.screens.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Состояния
    var hasCameraPermission by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }
    var cameraLens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    // Для создания файла
    val file = remember { createImageFile(context) }
    val uri = remember { FileProvider.getUriForFile(context, "${context.packageName}.provider", file) }

    // Исполнитель для камеры
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Launcher для разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Проверка разрешений при запуске
    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.CAMERA
        } else {
            Manifest.permission.CAMERA
        }

        val hasPermission = ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(permission)
        }
    }

    // Очистка при выходе
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Сфотографируйте еду") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка переключения камеры
                FloatingActionButton(
                    onClick = {
                        cameraLens = if (cameraLens == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Filled.Cameraswitch, contentDescription = "Переключить камеру")
                }

                // Кнопка вспышки
                FloatingActionButton(
                    onClick = { flashEnabled = !flashEnabled },
                    containerColor = if (flashEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        if (flashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Вспышка"
                    )
                }

                // Основная кнопка съемки
                FloatingActionButton(
                    onClick = {
                        takePhoto(
                            imageCapture = imageCapture,
                            outputFile = file,
                            executor = cameraExecutor,
                            flashEnabled = flashEnabled,
                            onSuccess = { onPhotoTaken(uri) },
                            onError = { /* Обработка ошибки */ }
                        )
                    }
                ) {
                    Icon(Icons.Filled.Camera, contentDescription = "Сфотографировать")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    cameraLens = cameraLens,
                    onCameraInitialized = { imageCapture = it }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Требуется разрешение на использование камеры")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Запросить разрешение")
                    }
                }
            }
        }
    }
}
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraLens: Int = CameraSelector.LENS_FACING_BACK,
    onCameraInitialized: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(cameraLens) {
        try {
            // Получаем CameraProvider синхронно
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()

            // Создаем preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Создаем ImageCapture
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Передаем наружу
            onCameraInitialized(imageCapture)

            // Создаем селектор камеры
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraLens)
                .build()

            // Освобождаем и привязываем
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        } catch (e: Exception) {
            e.printStackTrace()
            // Можно показать сообщение об ошибке
            Log.e("CameraPreview", "Ошибка инициализации камеры: ${e.message}")
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.externalCacheDir ?: context.cacheDir
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    ).apply {
        createNewFile()
    }
}

private fun takePhoto(
    imageCapture: ImageCapture?,
    outputFile: File,
    executor: ExecutorService,
    flashEnabled: Boolean,
    onSuccess: (Uri) -> Unit,
    onError: (Exception) -> Unit
) {
    imageCapture?.let { capture ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onSuccess(outputFileResults.savedUri ?: Uri.fromFile(outputFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }
}