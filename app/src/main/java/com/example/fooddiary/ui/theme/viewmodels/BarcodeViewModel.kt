package com.example.fooddiary.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.models.BarcodeProduct
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.data_old.repository.BarcodeRepository
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class BarcodeScanState {
    object Idle : BarcodeScanState()
    object Scanning : BarcodeScanState()
    data class FetchingProduct(val barcode: String) : BarcodeScanState()
    data class ProductFound(val result: BarcodeScanResult) : BarcodeScanState()
    data class Error(val message: String) : BarcodeScanState()
}

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val barcodeRepository: BarcodeRepository
) : ViewModel() {

    data class BarcodeUiState(
        val scanState: BarcodeScanState = BarcodeScanState.Idle,
        val scannedProducts: List<BarcodeProduct> = emptyList(),
        val lastScanResult: BarcodeScanResult? = null,
        val errorMessage: String? = null,
        val isScanning: Boolean = false,
        val cameraAvailable: Boolean = false
    )

    private val _uiState = MutableStateFlow(BarcodeUiState())
    val uiState: StateFlow<BarcodeUiState> = _uiState.asStateFlow()

    private var scanJob: Job? = null
    private var lastScannedBarcode: String? = null
    private var lastScanTime: Long = 0

    init {
        // Подписываемся на изменения в БД
        viewModelScope.launch {
            barcodeRepository.getRecentScannedProductsFlow()
                .collect { products ->
                    _uiState.update { it.copy(scannedProducts = products) }
                }
        }
    }

    fun setCameraAvailable(available: Boolean) {
        _uiState.update { it.copy(cameraAvailable = available) }
    }

    fun autoScanBarcode(bitmap: Bitmap) {
        // Предотвращение частого сканирования одного и того же
        if (_uiState.value.isScanning || _uiState.value.scanState !is BarcodeScanState.Idle) return

        scanBarcodeFromBitmap(bitmap)
    }

    fun scanBarcodeFromBitmap(bitmap: Bitmap) {
        if (scanJob?.isActive == true) return

        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanState = BarcodeScanState.Scanning) }

            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val barcodes = barcodeRepository.scanBarcode(image)

                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()

                    // Проверка "анти-дребезг" (3 секунды на тот же код)
                    val currentTime = System.currentTimeMillis()
                    if (barcode == lastScannedBarcode && (currentTime - lastScanTime < 3000)) {
                        _uiState.update { it.copy(isScanning = false, scanState = BarcodeScanState.Idle) }
                        return@launch
                    }

                    lastScannedBarcode = barcode
                    lastScanTime = currentTime

                    _uiState.update { it.copy(scanState = BarcodeScanState.FetchingProduct(barcode)) }

                    val result = barcodeRepository.fetchProductInfo(barcode)

                    if (result.product != null) {
                        _uiState.update {
                            it.copy(
                                scanState = BarcodeScanState.ProductFound(result),
                                lastScanResult = result,
                                errorMessage = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                scanState = BarcodeScanState.Error(result.error ?: "Не найден"),
                                lastScanResult = result, // Передаем результат даже с ошибкой, чтобы показать экран "Не найдено"
                                errorMessage = result.error
                            )
                        }
                    }
                } else {
                    // Штрихкод не найден на картинке - просто сбрасываем в Idle
                    _uiState.update { it.copy(scanState = BarcodeScanState.Idle) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    scanState = BarcodeScanState.Error(e.localizedMessage ?: "Ошибка"),
                    errorMessage = e.localizedMessage
                ) }
            } finally {
                _uiState.update { it.copy(isScanning = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                scanState = BarcodeScanState.Idle,
                lastScanResult = null // Сбрасываем результат, чтобы можно было сканировать снова
            )
        }
        lastScannedBarcode = null // Разрешаем повторное сканирование того же кода
    }

    fun saveProduct(product: BarcodeProduct) {
        viewModelScope.launch {
            barcodeRepository.saveScannedProduct(product)
        }
    }

    fun manualScanBarcode(bitmap: Bitmap) {
        if (scanJob?.isActive == true) {
            scanJob?.cancel()
        }
        scanBarcodeFromBitmap(bitmap)
    }

    fun resetScanner() {
        lastScannedBarcode = null
        lastScanTime = 0
        _uiState.update { it.copy(
            scanState = BarcodeScanState.Idle,
            lastScanResult = null,
            errorMessage = null
        ) }
    }
}