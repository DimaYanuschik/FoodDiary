package com.example.fooddiary.domain.usecase.foodrecognition

import android.graphics.Bitmap
import com.example.fooddiary.domain.model.foodrecognition.FoodRecognitionResult
import com.example.fooddiary.domain.repository.foodrecognition.IFoodRecognitionRepository

class RecognizeFoodUseCase(
    private val repository: IFoodRecognitionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<FoodRecognitionResult> {
        require(bitmap.width > 0 && bitmap.height > 0) { "Изображение пустое" }
        return repository.recognizeFood(bitmap)
    }

    suspend fun refine(bitmap: Bitmap, query: String): Result<FoodRecognitionResult> {
        require(bitmap.width > 0 && bitmap.height > 0) { "Изображение пустое" }
        require(query.isNotBlank()) { "Уточняющий запрос не может быть пустым" }
        return repository.refineRecognition(bitmap, query)
    }
}