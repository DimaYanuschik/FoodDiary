package com.example.fooddiary.data_old.models

import java.util.*

//data class CalorieGoal(
//    val id: String = "",
//    val userId: String = "",
//    val dailyCalories: Int = 0,
//    val proteinPercentage: Int = 30, // % от калорий
//    val fatPercentage: Int = 30,
//    val carbsPercentage: Int = 40,
//    val createdAt: Date = Date(),
//    val updatedAt: Date = Date()
//) {
//    val proteinGrams: Int get() = ((dailyCalories * proteinPercentage / 100.0) / 4).toInt()
//    val fatGrams: Int get() = ((dailyCalories * fatPercentage / 100.0) / 9).toInt()
//    val carbsGrams: Int get() = ((dailyCalories * carbsPercentage / 100.0) / 4).toInt()
//}

data class CalorieGoal(
    val id: String = "",
    val userId: String = "",
    val dailyCalories: Int = 0,
    // Режимы ввода для каждого макронутриента: "percent", "grams", "grams_per_kg"
    val proteinMode: String = "percent",
    val fatMode: String = "percent",
    val carbsMode: String = "percent",
    // Значения в процентах (0-100)
    val proteinPercent: Int = 30,
    val fatPercent: Int = 30,
    val carbsPercent: Int = 40,
    // Значения в граммах
    val proteinGrams: Int = 0,
    val fatGrams: Int = 0,
    val carbsGrams: Int = 0,
    // Значения в граммах на кг веса
    val proteinGramsPerKg: Double = 0.0,
    val fatGramsPerKg: Double = 0.0,
    val carbsGramsPerKg: Double = 0.0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)