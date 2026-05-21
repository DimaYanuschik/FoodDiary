package com.example.fooddiary.data.repository.water

import com.example.fooddiary.data.datasource.remote.dto.water.WaterEntryDto
import com.example.fooddiary.data.datasource.remote.water.WaterFirestoreDataSource
import com.example.fooddiary.data_old.repository.UserProfileRepository
import com.example.fooddiary.domain.model.water.WaterEntry
import com.example.fooddiary.domain.repository.water.IWaterRepository
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepositoryImpl @Inject constructor(
    private val dataSource: WaterFirestoreDataSource,
    private val userProfileRepository: UserProfileRepository
): IWaterRepository {

    override suspend fun addWater(amountMl: Int, date: Date, userId: String): WaterEntry {
        val dto = WaterEntryDto(amountMl = amountMl, date = date, userId = userId)
        val id = dataSource.addWater(dto)
        return WaterEntry(id = id, amountMl = amountMl, date = date, userId = userId)
    }

//    override suspend fun getWaterEntriesByDate(userId: String, date: Date): List<WaterEntry> {
//        return dataSource.getWaterEntriesByDate(userId, date).map { dto ->
//            WaterEntry(id = "", amountMl = dto.amountMl, date = dto.date, userId = dto.userId)
//        }
//    }

    override suspend fun getWaterEntriesByDate(userId: String, date: Date): List<WaterEntry> {
        return dataSource.getWaterEntriesByDate(userId, date).map { (id, dto) ->
            WaterEntry(id = id, amountMl = dto.amountMl, date = dto.date, userId = dto.userId)
        }
    }

    override suspend fun deleteWaterEntry(entryId: String) {
        dataSource.deleteWaterEntry(entryId)
    }

    override suspend fun getWaterGoal(userId: String): Int {
        val profile = userProfileRepository.getUserProfile(userId)
        return profile?.waterGoalMl ?: 0
    }

    override suspend fun setWaterGoal(userId: String, goalMl: Int) {
        val profile = userProfileRepository.getUserProfile(userId)
        if (profile != null) {
            userProfileRepository.saveUserProfile(profile.copy(waterGoalMl = goalMl))
        }
    }
}