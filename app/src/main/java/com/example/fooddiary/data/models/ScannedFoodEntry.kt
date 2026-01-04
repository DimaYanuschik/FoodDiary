package com.example.fooddiary.data.models

import java.util.*

data class ScannedFoodEntry(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val date: Date = Date(),
    val userId: String = "",
    val mealType: String = "Другое",
    val notes: String = "",
    val barcode: String? = null,
    val source: String = "barcode_scan",
    val originalProduct: BarcodeProduct? = null
)