package com.example.fooddiary.data.models

import java.util.*

data class CalorieGoal(
    val id: String = "",
    val userId: String = "",
    val dailyCalories: Int = 0,
    val proteinPercentage: Int = 30, // % от калорий
    val fatPercentage: Int = 30,
    val carbsPercentage: Int = 40,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    val proteinGrams: Int get() = ((dailyCalories * proteinPercentage / 100.0) / 4).toInt()
    val fatGrams: Int get() = ((dailyCalories * fatPercentage / 100.0) / 9).toInt()
    val carbsGrams: Int get() = ((dailyCalories * carbsPercentage / 100.0) / 4).toInt()
}