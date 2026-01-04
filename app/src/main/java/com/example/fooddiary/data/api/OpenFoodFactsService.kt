package com.example.fooddiary.data.api

import com.example.fooddiary.data.models.OpenFoodFactsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsService {
    @GET("api/v2/product/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): Response<OpenFoodFactsResponse>

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
    }
}
