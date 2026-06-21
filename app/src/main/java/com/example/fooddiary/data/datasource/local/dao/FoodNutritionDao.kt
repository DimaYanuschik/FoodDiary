package com.example.fooddiary.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fooddiary.data.datasource.local.entity.FoodNutritionEntity

@Dao
interface FoodNutritionDao {
    @Query("SELECT * FROM food_nutrition WHERE label = :label")
    suspend fun getNutrition(label: String): FoodNutritionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodNutritionEntity>)

    @Query("SELECT COUNT(*) FROM food_nutrition")
    suspend fun getCount(): Int

    @Query("DELETE FROM food_nutrition")
    suspend fun deleteAll()
}