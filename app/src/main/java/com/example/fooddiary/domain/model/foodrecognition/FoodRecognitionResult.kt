package com.example.fooddiary.domain.model.foodrecognition

data class FoodRecognitionResult(
    val name: String,
    val calories: Double,  // ккал на порцию
    val protein: Double,   // г
    val fat: Double,
    val carbs: Double,
    val confidence: Double, // 0..1
    val description: String // текстовое описание от модели
)