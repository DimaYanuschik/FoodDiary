package com.example.fooddiary.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.fooddiary.data.models.BarcodeProduct

@Database(
    entities = [BarcodeProduct::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BarcodeDatabase : RoomDatabase() {
    abstract fun barcodeProductDao(): BarcodeProductDao

    companion object {
        @Volatile
        private var INSTANCE: BarcodeDatabase? = null

        fun getDatabase(context: Context): BarcodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarcodeDatabase::class.java,
                    "barcode_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
