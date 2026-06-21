package com.example.fooddiary.data.repository.foodrecognition

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.fooddiary.data.datasource.local.dao.FoodNutritionDao
import com.example.fooddiary.data.datasource.local.entity.FoodNutritionEntity
import com.example.fooddiary.data.datasource.local.foodrecognition.LocalFoodRecognitionDataSource
import com.example.fooddiary.domain.model.foodrecognition.LocalFoodRecognitionResult
import com.example.fooddiary.domain.repository.foodrecognition.ILocalFoodRecognitionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.atomic.AtomicBoolean

@Singleton
class LocalFoodRecognitionRepositoryImpl @Inject constructor(
    private val dataSource: LocalFoodRecognitionDataSource,
    private val nutritionDao: FoodNutritionDao,
    @ApplicationContext private val context: Context
) : ILocalFoodRecognitionRepository {

    companion object {
        private const val EXPECTED_NUTRITION_COUNT = 900 // кол-во строк в json
        private const val TAG = "LocalFoodRecognitionRepo"
    }
    private val isNutritionLoading = AtomicBoolean(false)
    @Volatile
    private var isNutritionLoaded = false

    private suspend fun ensureNutritionLoaded() {
        if (isNutritionLoaded) return

        if (isNutritionLoading.compareAndSet(false, true)) {
            try {
                val existingCount = nutritionDao.getCount()
                if (existingCount == 0 || existingCount != EXPECTED_NUTRITION_COUNT) {
                    val json = context.assets.open("food_nutrition.json") // заменил имя на актуальное
                        .bufferedReader()
                        .readText()
                    val type = object : TypeToken<List<FoodNutritionEntity>>() {}.type
                    val entries: List<FoodNutritionEntity> = Gson().fromJson(json, type)
                    // Очищаем старые данные и вставляем новые
                    nutritionDao.deleteAll()
                    nutritionDao.insertAll(entries)
                    Log.d(TAG, "База КБЖУ обновлена, загружено ${entries.size} записей")
                }
                isNutritionLoaded = true
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка загрузки КБЖУ", e)
            } finally {
                // не сбрасываем isNutritionLoading, чтобы не дублировать загрузку
            }
        }
    }

    override suspend fun recognize(bitmap: Bitmap): Result<LocalFoodRecognitionResult> {
        ensureNutritionLoaded()   // загружаем один раз

        return try {
            val (label, confidence) = dataSource.recognize(bitmap)
            val nutrition = nutritionDao.getNutrition(label)
            if (nutrition != null) {
                Result.success(
                    LocalFoodRecognitionResult(
                        name = label,
                        calories = nutrition.calories,
                        protein = nutrition.protein,
                        fat = nutrition.fat,
                        carbs = nutrition.carbs,
                        confidence = confidence
                    )
                )
            } else {
                Result.failure(Exception("КБЖУ не найдены для продукта \"$label\""))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}