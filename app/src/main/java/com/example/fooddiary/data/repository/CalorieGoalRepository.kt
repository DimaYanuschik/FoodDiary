package com.example.fooddiary.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.fooddiary.data.models.CalorieGoal
import kotlinx.coroutines.tasks.await
import java.util.*

class CalorieGoalRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    companion object {
        const val COLLECTION_CALORIE_GOALS = "calorieGoals"
    }

    suspend fun saveCalorieGoal(goal: CalorieGoal): String {
        val existingGoal = getCalorieGoal(goal.userId)

        val goalRef = if (existingGoal != null && existingGoal.id.isNotEmpty()) {
            db.collection(COLLECTION_CALORIE_GOALS).document(existingGoal.id)
        } else {
            db.collection(COLLECTION_CALORIE_GOALS).document()
        }

        val goalToSave = goal.copy(
            id = goalRef.id,
            updatedAt = Date()
        )

        goalRef.set(goalToSave).await()
        return goalRef.id
    }

    suspend fun getCalorieGoal(userId: String): CalorieGoal? {
        return try {
            val snapshot = db.collection(COLLECTION_CALORIE_GOALS)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(CalorieGoal::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteCalorieGoal(userId: String) {
        try {
            val snapshot = db.collection(COLLECTION_CALORIE_GOALS)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
        } catch (e: Exception) {
        }
    }
}