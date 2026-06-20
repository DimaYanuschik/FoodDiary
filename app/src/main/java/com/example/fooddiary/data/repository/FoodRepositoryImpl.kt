package com.example.fooddiary.data.repository

import android.util.Log
import com.example.fooddiary.data.datasource.local.dao.FoodEntryDao
import com.example.fooddiary.data.datasource.local.entity.FoodEntryEntity
import com.example.fooddiary.data_old.repository.DailyStats
import com.example.fooddiary.data_old.repository.FoodEntry
import com.example.fooddiary.data_old.repository.FoodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val firestoreFoodRepository: FoodRepository
) {
    companion object {
        private const val TAG = "FoodRepo"
    }

    // Скоуп для фоновых операций с Firestore
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun addFoodEntry(entry: FoodEntry, userId: String): String {
        return withContext(Dispatchers.IO) {
            val entity = mapToEntity(entry)
            foodEntryDao.insert(entity)
            Log.d(TAG, "Сохранено в Room: ${entry.id}")


            syncScope.launch {
                try {
                    firestoreFoodRepository.addFoodEntry(entry, userId)
                    Log.d(TAG, "Синхронизировано с Firestore: ${entry.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка синхронизации: ${e.message}")
                }
            }
            entry.id
        }
    }

    // Теперь читаем ТОЛЬКО из Room, без обращений к Firestore
    suspend fun getFoodEntriesByDate(userId: String, date: Date): List<FoodEntry> {
        return withContext(Dispatchers.IO) {
            val startOfDay = getStartOfDayTimestamp(date)
            val entries = foodEntryDao.getEntriesByDate(userId, startOfDay).map { mapToDomain(it) }
            Log.d(TAG, "Загружено из Room: ${entries.size} записей за $date")
            entries
        }
    }

    suspend fun getFoodEntriesByWeek(userId: String, startOfWeek: Date): List<FoodEntry> {
        return withContext(Dispatchers.IO) {
            val startTs = getStartOfDayTimestamp(startOfWeek)
            val cal = Calendar.getInstance().apply { time = startOfWeek; add(Calendar.DAY_OF_MONTH, 7) }
            val endTs = getStartOfDayTimestamp(cal.time)
            val entries = foodEntryDao.getEntriesByWeek(userId, startTs, endTs).map { mapToDomain(it) }
            Log.d(TAG, "Загружено из Room за неделю: ${entries.size} записей")
            entries
        }
    }

    suspend fun deleteFoodEntry(entryId: String) {
        withContext(Dispatchers.IO) {
            foodEntryDao.deleteById(entryId)

            syncScope.launch {
                try {
                    firestoreFoodRepository.deleteFoodEntry(entryId)
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка удаления из Firestore: ${e.message}")
                }
            }
        }
    }

    suspend fun getDailyStats(userId: String, date: Date): DailyStats {
        val entries = getFoodEntriesByDate(userId, date)
        return DailyStats(
            date = date,
            totalCalories = entries.sumOf { it.calories },
            totalProtein = entries.sumOf { it.protein },
            totalFat = entries.sumOf { it.fat },
            totalCarbs = entries.sumOf { it.carbs },
            userId = userId
        )
    }

    suspend fun getDailyStatsForWeek(userId: String, startOfWeek: Date): List<DailyStats> {
        val cal = Calendar.getInstance().apply { time = startOfWeek }
        val stats = mutableListOf<DailyStats>()
        repeat(7) {
            val date = cal.time
            stats.add(getDailyStats(userId, date))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return stats
    }

    // Явная синхронизация с Firestore – вызывается при старте
    suspend fun syncFromFirestore(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Начинаем синхронизацию с Firestore для userId=$userId")
                val firestoreEntries = firestoreFoodRepository.getFoodEntries(userId)
                if (firestoreEntries.isNotEmpty()) {
                    val entities = firestoreEntries.map { mapToEntity(it) }
                    foodEntryDao.insertAll(entities)
                    Log.d(TAG, "Синхронизировано ${entities.size} записей из Firestore")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка синхронизации с Firestore: ${e.message}")
            }
        }
    }

    // ---------- Маппинг ----------
    private fun mapToEntity(entry: FoodEntry): FoodEntryEntity = FoodEntryEntity(
        id = entry.id,
        name = entry.name,
        calories = entry.calories,
        protein = entry.protein,
        fat = entry.fat,
        carbs = entry.carbs,
        date = getStartOfDayTimestamp(entry.date),
        userId = entry.userId,
        mealType = entry.mealType
    )

    private fun mapToDomain(entity: FoodEntryEntity): FoodEntry = FoodEntry(
        id = entity.id,
        name = entity.name,
        calories = entity.calories,
        protein = entity.protein,
        fat = entity.fat,
        carbs = entity.carbs,
        date = Date(entity.date),
        userId = entity.userId,
        mealType = entity.mealType
    )

    private fun getStartOfDayTimestamp(date: Date): Long {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}