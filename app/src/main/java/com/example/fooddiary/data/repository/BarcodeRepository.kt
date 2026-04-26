package com.example.fooddiary.data.repository

import android.content.Context
import android.util.Log
import com.example.fooddiary.data.api.ApiClient
import com.example.fooddiary.data.database.BarcodeDatabase
import com.example.fooddiary.data.models.BarcodeProduct
import com.example.fooddiary.data.models.BarcodeScanResult
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeRepository @Inject constructor(
    private val context: Context,
    private val apiClient: ApiClient,
    private val barcodeDatabase: BarcodeDatabase
) {
    private val tag = "BarcodeRepository"

    // Оптимизация сканера: только нужные форматы для продуктов
    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E
            )
            .build()
    )

    private val dao = barcodeDatabase.barcodeProductDao()

    suspend fun scanBarcode(image: InputImage): List<String> = withContext(Dispatchers.Default) {
        try {
            val barcodes = barcodeScanner.process(image).await()
            val result = barcodes.mapNotNull { it.rawValue }
            if (result.isNotEmpty()) Log.d(tag, "Найдено штрихкодов: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e(tag, "Ошибка ML Kit", e)
            emptyList()
        }
    }

    suspend fun fetchProductInfo(barcode: String): BarcodeScanResult {
        Log.d(tag, "Поиск продукта: $barcode")

        // 1. Локальная база
        try {
            val localProduct = dao.getByBarcode(barcode)
            if (localProduct != null) {
                Log.d(tag, "Найден в локальной БД: ${localProduct.name}")
                return BarcodeScanResult(barcode, localProduct)
            }
        } catch (e: Exception) {
            Log.e(tag, "Ошибка БД", e)
        }

        // 2. OpenFoodFacts API
        val offProduct = fetchFromOpenFoodFacts(barcode)
        if (offProduct != null) {
            saveScannedProduct(offProduct) // Сохраняем в кэш
            return BarcodeScanResult(barcode, offProduct)
        }

        return BarcodeScanResult(barcode, error = "Продукт не найден")
    }

    private suspend fun fetchFromOpenFoodFacts(barcode: String): BarcodeProduct? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiClient.openFoodFactsService.getProductByBarcode(barcode)
                val body = response.body()

                if (response.isSuccessful && body?.status == 1 && body.product != null) {
                    val p = body.product

                    // Приоритет русскому названию для СНГ товаров
                    val name = p.productNameRu?.takeIf { it.isNotBlank() }
                        ?: p.productName
                        ?: "Продукт $barcode"

                    val nutriments = p.nutriments

                    BarcodeProduct(
                        barcode = barcode,
                        name = name,
                        brand = p.brands,
                        calories = nutriments?.energyKcal100g?.toInt() ?: 0,
                        protein = nutriments?.proteins100g ?: 0.0,
                        fat = nutriments?.fat100g ?: 0.0,
                        carbs = nutriments?.carbohydrates100g ?: 0.0,
                        imageUrl = p.imageUrl,
                        country = p.countries,
                        categories = p.categories?.split(",")?.map { it.trim() } ?: emptyList(),
                        source = "openfoodfacts"
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Ошибка OpenFoodFacts API", e)
                null
            }
        }
    }

    suspend fun saveScannedProduct(product: BarcodeProduct) {
        withContext(Dispatchers.IO) {
            dao.insert(product)
        }
    }

    // Возвращаем Flow для реактивного UI
    fun getRecentScannedProductsFlow() = dao.getAll()
}