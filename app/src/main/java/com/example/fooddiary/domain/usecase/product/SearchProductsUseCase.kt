package com.example.fooddiary.domain.usecase.product

import com.example.fooddiary.domain.model.product.Product
import com.example.fooddiary.domain.repository.product.IProductRepository

class SearchProductsUseCase(
    private val productRepository: IProductRepository
) {
    suspend operator fun invoke(query: String): List<Product> {
        if (query.isBlank())
            return emptyList()

        return productRepository.searchProducts(query.trim())
    }
}