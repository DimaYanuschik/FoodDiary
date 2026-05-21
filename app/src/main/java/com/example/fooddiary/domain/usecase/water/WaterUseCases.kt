package com.example.fooddiary.domain.usecase.water

import com.example.fooddiary.domain.model.water.WaterEntry
import com.example.fooddiary.domain.repository.water.IWaterRepository
import java.util.Date

class AddWaterUseCase(private val repository: IWaterRepository) {
    suspend operator fun invoke(amountMl: Int, date: Date, userId: String): WaterEntry {
        require(amountMl > 0) { "Объем воды должен быть положительным" }
        return repository.addWater(amountMl, date, userId)
    }
}

class GetWaterProgressUseCase(private val repository: IWaterRepository) {
    suspend operator fun invoke(userId: String, date: Date): List<WaterEntry> {
        return repository.getWaterEntriesByDate(userId, date)
    }
}

class DeleteWaterEntryUseCase(private val repository: IWaterRepository) {
    suspend operator fun invoke(entryId: String) {
        repository.deleteWaterEntry(entryId)
    }
}

class GetWaterGoalUseCase(private val repository: IWaterRepository) {
    suspend operator fun invoke(userId: String): Int {
        return repository.getWaterGoal(userId)
    }
}

class SetWaterGoalUseCase(private val repository: IWaterRepository) {
    suspend operator fun invoke(userId: String, goalMl: Int) {
        require(goalMl >= 0) { "Цель не может быть отрицательной" }
        repository.setWaterGoal(userId, goalMl)
    }
}