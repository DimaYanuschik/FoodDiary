package com.example.fooddiary.data.datasource.remote.product

import com.example.fooddiary.data.datasource.remote.dto.product.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsSearchApi {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("json") json: Int = 1
    ): SearchResponseDto
}