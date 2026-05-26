package com.example.fooddiary.data.repository.foodrecognition

import android.graphics.Bitmap
import com.example.fooddiary.data.datasource.remote.foodrecognition.GroqFoodRecognitionDataSource
import com.example.fooddiary.domain.model.foodrecognition.FoodRecognitionResult
import com.example.fooddiary.domain.repository.foodrecognition.IFoodRecognitionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRecognitionRepositoryImpl @Inject constructor(
    private val dataSource: GroqFoodRecognitionDataSource
) : IFoodRecognitionRepository {
    override suspend fun recognizeFood(bitmap: Bitmap): Result<FoodRecognitionResult> {
        return dataSource.recognizeFood(bitmap)
    }

    override suspend fun refineRecognition(bitmap: Bitmap, query: String): Result<FoodRecognitionResult> {
        return dataSource.refineRecognition(bitmap, query)
    }
}