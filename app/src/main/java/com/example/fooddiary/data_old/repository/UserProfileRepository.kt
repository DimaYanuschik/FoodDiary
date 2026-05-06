package com.example.fooddiary.data_old.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.fooddiary.data_old.models.UserProfile
import kotlinx.coroutines.tasks.await
import java.util.*

class UserProfileRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    companion object {
        const val COLLECTION_USER_PROFILES = "userProfiles"
    }

    suspend fun saveUserProfile(profile: UserProfile): String {
        return try {
            // Проверяем, существует ли уже профиль для этого пользователя
            val existingProfile = getUserProfile(profile.userId)

            val profileToSave: UserProfile
            val profileRef = if (existingProfile != null && existingProfile.id.isNotEmpty()) {
                // Обновляем существующий профиль - используем тот же ID
                profileToSave = profile.copy(
                    id = existingProfile.id,
                    createdAt = existingProfile.createdAt, // Сохраняем оригинальную дату создания
                    updatedAt = Date()
                )
                db.collection(COLLECTION_USER_PROFILES).document(existingProfile.id)
            } else {
                // Создаем новый профиль с новым ID
                val newDocRef = db.collection(COLLECTION_USER_PROFILES).document()
                profileToSave = profile.copy(
                    id = newDocRef.id,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                newDocRef
            }
            profileRef.set(profileToSave).await()
            profileToSave.id
        } catch (e: Exception) {
            throw Exception("Ошибка сохранения профиля: ${e.message}")
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = db.collection(COLLECTION_USER_PROFILES)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { document ->
                document.toObject(UserProfile::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserProfileById(profileId: String): UserProfile? {
        return try {
            val document = db.collection(COLLECTION_USER_PROFILES)
                .document(profileId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject(UserProfile::class.java)?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteUserProfile(userId: String): Boolean {
        return try {
            val snapshot = db.collection(COLLECTION_USER_PROFILES)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
            true
        } catch (e: Exception) {
            false
        }
    }
}