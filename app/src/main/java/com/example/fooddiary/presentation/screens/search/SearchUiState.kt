package com.example.fooddiary.presentation.screens.search

import com.example.fooddiary.domain.model.product.Product


data class SearchUiState(
    val query: String = "",
    val history: List<String> = emptyList(),
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)