package com.example.fooddiary.domain.model.product

data class Product (
    val id: String,
    val name: String,
    val brand: String?,
    val caloriesPer100g: Double,
    val proteinsPer100g: Double?,
    val fatsPer100g: Double?,
    val carbsPer100g: Double?,
    val imageUrl: String?
)