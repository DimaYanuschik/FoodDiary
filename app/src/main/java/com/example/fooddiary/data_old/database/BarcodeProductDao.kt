package com.example.fooddiary.data_old.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fooddiary.data_old.models.BarcodeProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: BarcodeProduct)

    @Query("SELECT * FROM barcode_products WHERE barcode = :barcode")
    suspend fun getByBarcode(barcode: String): BarcodeProduct?

    @Query("SELECT * FROM barcode_products ORDER BY lastUpdated DESC")
    fun getAll(): Flow<List<BarcodeProduct>>

    @Query("DELETE FROM barcode_products WHERE barcode = :barcode")
    suspend fun delete(barcode: String)

    @Query("SELECT COUNT(*) FROM barcode_products")
    suspend fun getCount(): Int
}