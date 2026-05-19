package com.example.fooddiary.data.datasource.remote.product

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val COLLECTION_USER_PROFILES = "userProfiles"
        const val SUBCOLLECTION_SEARCH_HISTORY = "searchHistory"
    }

    suspend fun addQuery(userId: String, query: String) {
        val profileSnapshot = firestore.collection(COLLECTION_USER_PROFILES)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()
        val profileDocId = profileSnapshot.documents.firstOrNull()?.id ?: return

        val data = hashMapOf(
            "query" to query,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        firestore.collection(COLLECTION_USER_PROFILES)
            .document(profileDocId)
            .collection(SUBCOLLECTION_SEARCH_HISTORY)
            .add(data)
            .await()
    }

    suspend fun getAllQueries(userId: String): List<String> {
        val profileSnapshot = firestore.collection(COLLECTION_USER_PROFILES)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()
        val profileDocId = profileSnapshot.documents.firstOrNull()?.id ?: return emptyList()

        val historySnapshot = firestore.collection(COLLECTION_USER_PROFILES)
            .document(profileDocId)
            .collection(SUBCOLLECTION_SEARCH_HISTORY)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return historySnapshot.documents.mapNotNull { it.getString("query") }
    }
}