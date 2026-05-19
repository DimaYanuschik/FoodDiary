package com.example.fooddiary.data.mapper

import com.example.fooddiary.data.datasource.local.entity.ProductEntity
import com.example.fooddiary.data.datasource.remote.dto.product.SearchProductDto
import com.example.fooddiary.domain.model.product.Product

// DTO -> Entity
fun SearchProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = code ?: "",
        name = productNameRu?.takeIf { it.isNotBlank() } ?: productName ?: "Неизвестный продукт",
        brand = brands,
        caloriesPer100g = nutriments?.energyKcal100g ?: 0.0,
        proteinsPer100g = nutriments?.proteins100g,
        fatsPer100g = nutriments?.fat100g,
        carbsPer100g = nutriments?.carbohydrates100g,
        imageUrl = imageUrl
    )
}

// Entity -> Domain
fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        brand = brand,
        caloriesPer100g = caloriesPer100g,
        proteinsPer100g = proteinsPer100g,
        fatsPer100g = fatsPer100g,
        carbsPer100g = carbsPer100g,
        imageUrl = imageUrl
    )
}