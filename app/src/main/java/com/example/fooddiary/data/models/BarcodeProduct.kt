package com.example.fooddiary.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

// 1. Основная сущность БД
@Entity(tableName = "barcode_products")
data class BarcodeProduct(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val calories: Int = 0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val servingSize: String? = "100g",
    val imageUrl: String? = null,
    val country: String? = null,
    val categories: List<String> = emptyList(),
    val lastUpdated: Date = Date(),
    val source: String = "openfoodfacts"
)

// 2. Результат сканирования (для UI)
data class BarcodeScanResult(
    val barcode: String,
    val product: BarcodeProduct? = null,
    val error: String? = null,
    val timestamp: Date = Date()
)

// Классы для OpenFoodFacts

// Корневой ответ от сервера
data class OpenFoodFactsResponse(
    @SerializedName("code") val code: String? = null,
    @SerializedName("product") val product: OpenFoodFactsProductDetails? = null,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("status_verbose") val statusVerbose: String? = null
)

// Детали продукта внутри ответа
data class OpenFoodFactsProductDetails(
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("product_name_ru") val productNameRu: String? = null,
    @SerializedName("brands") val brands: String? = null,
    @SerializedName("nutriments") val nutriments: Nutriments? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("countries") val countries: String? = null,
    @SerializedName("categories") val categories: String? = null
)

// БЖУ
data class Nutriments(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Double? = null,
    @SerializedName("proteins_100g") val proteins100g: Double? = null,
    @SerializedName("fat_100g") val fat100g: Double? = null,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Double? = null
)