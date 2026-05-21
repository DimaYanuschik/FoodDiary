package com.example.fooddiary.domain.repository.water

import com.example.fooddiary.domain.model.water.WaterEntry
import java.util.Date

interface IWaterRepository {
    suspend fun addWater(amountMl: Int, date: Date, userId: String): WaterEntry
    suspend fun getWaterEntriesByDate(userId: String, date: Date): List<WaterEntry>
    suspend fun deleteWaterEntry(entryId: String)
    suspend fun getWaterGoal(userId: String): Int
    suspend fun setWaterGoal(userId: String, goalMl: Int)
}