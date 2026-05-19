package com.example.fooddiary.data.datasource.remote.dto.product

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("products") val products: List<SearchProductDto>
)

data class SearchProductDto(
    @SerializedName("code") val code: String?,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("product_name_ru") val productNameRu: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("nutriments") val nutriments: NutrimentsDto?,
    @SerializedName("image_url") val imageUrl: String?
)

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Double?,
    @SerializedName("proteins_100g") val proteins100g: Double?,
    @SerializedName("fat_100g") val fat100g: Double?,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Double?
)