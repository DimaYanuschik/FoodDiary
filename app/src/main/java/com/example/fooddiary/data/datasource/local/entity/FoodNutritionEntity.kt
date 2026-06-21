package com.example.fooddiary.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_nutrition")
data class FoodNutritionEntity(
    @PrimaryKey
    val label: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)