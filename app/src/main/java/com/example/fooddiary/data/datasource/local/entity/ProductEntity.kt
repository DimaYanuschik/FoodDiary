package com.example.fooddiary.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cached_products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String?,
    val caloriesPer100g: Double,
    val proteinsPer100g: Double?,
    val fatsPer100g: Double?,
    val carbsPer100g: Double?,
    val imageUrl: String?
)