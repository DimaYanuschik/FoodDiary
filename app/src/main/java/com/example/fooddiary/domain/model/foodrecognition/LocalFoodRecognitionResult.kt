package com.example.fooddiary.domain.model.foodrecognition

data class LocalFoodRecognitionResult(
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val confidence: Float
)