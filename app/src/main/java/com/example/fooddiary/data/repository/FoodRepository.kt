package com.example.fooddiary.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

data class FoodEntry(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val date: Date = Date(),
    val userId: String = "",
    val mealType: String = "Завтрак" // Завтрак, Обед, Ужин, Перекус
)

data class DailyStats(
    val date: Date = Date(),
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalFat: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val userId: String = ""
)

class FoodRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addFoodEntry(food: FoodEntry, userId: String): String {
        val foodWithId = db.collection("foodEntries").document()
        val foodData = food.copy(id = foodWithId.id, userId = userId)

        foodWithId.set(foodData).await()
        return foodWithId.id
    }

    suspend fun getFoodEntries(userId: String): List<FoodEntry> {
        val snapshot = db.collection("foodEntries")
            .whereEqualTo("userId", userId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            doc.toObject(FoodEntry::class.java) ?: FoodEntry()
        }
    }

    suspend fun getFoodEntriesByDate(userId: String, date: Date): List<FoodEntry> {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time

        val snapshot = db.collection("foodEntries")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThan("date", endOfDay)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            doc.toObject(FoodEntry::class.java) ?: FoodEntry()
        }
    }

    suspend fun deleteFoodEntry(foodId: String) {
        db.collection("foodEntries").document(foodId).delete().await()
    }

    suspend fun getDailyStats(userId: String, date: Date): DailyStats {
        val entries = getFoodEntriesByDate(userId, date)

        val totalCalories = entries.sumOf { it.calories }
        val totalProtein = entries.sumOf { it.protein }
        val totalFat = entries.sumOf { it.fat }
        val totalCarbs = entries.sumOf { it.carbs }

        return DailyStats(
            date = date,
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCarbs = totalCarbs,
            userId = userId
        )
    }

    suspend fun getDailyStatsForWeek(userId: String): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val stats = mutableListOf<DailyStats>()

        // Получаем статистику за последние 7 дней
        repeat(7) {
            val date = calendar.time
            stats.add(getDailyStats(userId, date))
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        return stats.reversed() // Сначала старые даты
    }
}