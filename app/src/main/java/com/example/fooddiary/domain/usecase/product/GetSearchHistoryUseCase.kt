package com.example.fooddiary.domain.usecase.product

import com.example.fooddiary.domain.repository.product.IProductRepository

class GetSearchHistoryUseCase(
    private val repository: IProductRepository
) {
    suspend operator fun invoke(): List<String> = repository.getSearchHistory()
}