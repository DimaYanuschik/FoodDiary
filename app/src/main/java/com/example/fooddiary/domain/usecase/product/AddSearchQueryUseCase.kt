package com.example.fooddiary.domain.usecase.product

import com.example.fooddiary.domain.repository.product.IProductRepository

class AddSearchQueryUseCase(
    private val repository: IProductRepository
) {
    suspend operator fun invoke(query: String) {
        if(query.isNotBlank()) repository.addSearchQuery(query.trim())
    }
}