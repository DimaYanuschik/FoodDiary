package com.example.fooddiary.data.datasource.local.database

import androidx.room.TypeConverter
import java.util.*

// Копия кода из data_old...Converters
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}