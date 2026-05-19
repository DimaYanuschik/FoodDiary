package com.example.fooddiary.data.datasource.local.dao

import androidx.room.*
import com.example.fooddiary.data.datasource.local.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentQueries(limit: Int): List<SearchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query: SearchHistoryEntity)
}