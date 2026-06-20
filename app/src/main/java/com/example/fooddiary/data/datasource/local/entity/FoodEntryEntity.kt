package com.example.fooddiary.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val date: Long,
    val userId: String,
    val mealType: String
)