package com.example.fooddiary.data_old.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

data class FoodEntry(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val date: Date = Date(),
    val userId: String = "",
    val mealType: String = "Завтрак"
)

data class DailyStats(
    val date: Date = Date(),
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalFat: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val userId: String = ""
)

@Singleton
class FoodRepository @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore

    companion object {
        const val COLLECTION_FOOD_ENTRIES = "foodEntries"
    }

//    suspend fun addFoodEntry(food: FoodEntry, userId: String): String {
//        val foodWithId = db.collection(COLLECTION_FOOD_ENTRIES).document()
//        val foodData = food.copy(
//            id = foodWithId.id,
//            userId = userId,
////            date = Date()
//        )
//
//        foodWithId.set(foodData).await()
//        return foodWithId.id
//    }
    suspend fun addFoodEntry(food: FoodEntry, userId: String): String {
        // Используем food.id как ID документа, а не создаём новый
        val docRef = db.collection(COLLECTION_FOOD_ENTRIES).document(food.id)
        val foodData = food.copy(userId = userId)
        docRef.set(foodData).await()
        return food.id
    }

    suspend fun getFoodEntries(userId: String): List<FoodEntry> {
        val snapshot = db.collection(COLLECTION_FOOD_ENTRIES)
            .whereEqualTo("userId", userId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(FoodEntry::class.java)
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

        try {
            val snapshot = db.collection(COLLECTION_FOOD_ENTRIES)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThan("date", endOfDay)
                .get()
                .await()

            return snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodEntry::class.java)
            }
        } catch (e: Exception) {
            // Если нет индекса, возвращаем пустой список
            return emptyList()
        }
    }

    suspend fun getTodayFoodEntries(userId: String): List<FoodEntry> {
        return getFoodEntriesByDate(userId, Date())
    }

    suspend fun deleteFoodEntry(foodId: String) {
        db.collection(COLLECTION_FOOD_ENTRIES).document(foodId).delete().await()
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

        return stats.reversed()
    }

    // Для произвольной недели
    suspend fun getDailyStatsForWeek(userId: String, weekStart: Date): List<DailyStats> {
        val calendar = Calendar.getInstance().apply { time = weekStart }
        val stats = mutableListOf<DailyStats>()
        repeat(7) {
            val date = calendar.time
            stats.add(getDailyStats(userId, date))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return stats
    }

    suspend fun testQueries(userId: String) {
        try {
            // Тест запроса 1
            val test1 = db.collection("foodEntries")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            Log.d("FirestoreTest", "Запрос 1 выполнен успешно")

            // Тест запроса 2
            val today = Date()
            val calendar = Calendar.getInstance().apply {
                time = today
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.time

            val test2 = db.collection("foodEntries")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .limit(1)
                .get()
                .await()
            Log.d("FirestoreTest", "Запрос 2 выполнен успешно")

        } catch (e: Exception) {
            Log.e("FirestoreTest", "Ошибка запроса: ${e.message}")
            if (e.message?.contains("index") == true) {
                Log.e("FirestoreTest", "⚠️ Нужно создать индекс в Firebase Console!")
            }
        }
    }
}

