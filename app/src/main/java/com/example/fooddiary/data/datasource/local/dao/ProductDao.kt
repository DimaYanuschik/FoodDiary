package com.example.fooddiary.data.datasource.local.dao

import androidx.room.*
import com.example.fooddiary.data.datasource.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM cached_products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM cached_products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    suspend fun searchByNameOrBrand(query: String): List<ProductEntity>
}