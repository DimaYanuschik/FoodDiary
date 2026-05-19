package com.example.fooddiary.data.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fooddiary.data.datasource.local.dao.ProductDao
import com.example.fooddiary.data.datasource.local.dao.SearchHistoryDao
import com.example.fooddiary.data.datasource.local.entity.ProductEntity
import com.example.fooddiary.data.datasource.local.entity.SearchHistoryEntity

@Database(
    entities = [ProductEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun SearchHistoryDao(): SearchHistoryDao
}
