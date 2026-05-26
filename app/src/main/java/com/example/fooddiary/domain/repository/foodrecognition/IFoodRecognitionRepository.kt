package com.example.fooddiary.domain.repository.foodrecognition

import android.graphics.Bitmap
import com.example.fooddiary.domain.model.foodrecognition.FoodRecognitionResult

interface IFoodRecognitionRepository {
    suspend fun recognizeFood(bitmap: Bitmap): Result<FoodRecognitionResult>
    suspend fun refineRecognition(bitmap: Bitmap, query: String): Result<FoodRecognitionResult>
}