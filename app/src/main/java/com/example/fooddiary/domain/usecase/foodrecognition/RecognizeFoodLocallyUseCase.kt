package com.example.fooddiary.domain.usecase.foodrecognition

import android.graphics.Bitmap
import com.example.fooddiary.domain.model.foodrecognition.LocalFoodRecognitionResult
import com.example.fooddiary.domain.repository.foodrecognition.ILocalFoodRecognitionRepository

class RecognizeFoodLocallyUseCase(
    private val repository: ILocalFoodRecognitionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<LocalFoodRecognitionResult> {
        require(bitmap.width > 0 && bitmap.height > 0) { "Изображение не должно быть пустым" }
        return repository.recognize(bitmap)
    }
}