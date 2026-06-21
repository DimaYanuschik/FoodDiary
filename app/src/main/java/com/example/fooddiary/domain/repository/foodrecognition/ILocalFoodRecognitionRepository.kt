package com.example.fooddiary.domain.repository.foodrecognition

import android.graphics.Bitmap
import com.example.fooddiary.domain.model.foodrecognition.LocalFoodRecognitionResult

interface ILocalFoodRecognitionRepository {
    suspend fun recognize(bitmap: Bitmap): Result<LocalFoodRecognitionResult>
}