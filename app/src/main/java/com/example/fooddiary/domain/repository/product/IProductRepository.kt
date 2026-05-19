package com.example.fooddiary.domain.repository.product

import com.example.fooddiary.domain.model.product.Product

interface IProductRepository {
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getSearchHistory(limit: Int = 100): List<String>
    suspend fun addSearchQuery(query: String)
}