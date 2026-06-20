package com.example.fooddiary.data.datasource.local.dao

import androidx.room.*
import com.example.fooddiary.data.datasource.local.entity.FoodEntryEntity

@Dao
interface FoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodEntryEntity)

    @Query("SELECT * FROM food_entries WHERE userId = :userId AND date = :date")
    suspend fun getEntriesByDate(userId: String, date: Long): List<FoodEntryEntity>

    @Query("SELECT * FROM food_entries WHERE userId = :userId AND date >= :startOfWeek AND date < :endOfWeek")
    suspend fun getEntriesByWeek(userId: String, startOfWeek: Long, endOfWeek: Long): List<FoodEntryEntity>

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM food_entries WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}