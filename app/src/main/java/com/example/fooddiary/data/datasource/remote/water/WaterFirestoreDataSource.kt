package com.example.fooddiary.data.datasource.remote.water

import android.icu.util.Calendar
import android.util.Log
import com.example.fooddiary.data.datasource.remote.dto.water.WaterEntryDto
import com.example.fooddiary.domain.model.water.WaterEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WaterFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){
    companion object {
        const val COLLECTION_WATER = "waterEntries"
        private const val TAG = "WaterFirestoreDS"
    }

    suspend fun addWater(entry: WaterEntryDto): String {
        val docRef = firestore.collection(COLLECTION_WATER).document()
        docRef.set(entry).await()
        Log.d(TAG, "Добавлена запись воды с id: ${docRef.id}, amountMl=${entry.amountMl}")
        return docRef.id
    }

//    suspend fun getWaterEntriesByDate(userId: String, date: Date): List<WaterEntryDto> {
//        val calendar = Calendar.getInstance().apply { time = date }
//        calendar.set(Calendar.HOUR_OF_DAY, 0)
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 0)
//        calendar.set(Calendar.MILLISECOND, 0)
//        val startOfDay = calendar.time
//        calendar.add(Calendar.DAY_OF_MONTH, 1)
//        val endOfDay = calendar.time
//
//        val snapshot = firestore.collection(COLLECTION_WATER)
//            .whereEqualTo("userId", userId)
//            .whereGreaterThanOrEqualTo("date", startOfDay)
//            .whereLessThan("date", endOfDay)
//            .get()
//            .await()
//
//        return snapshot.documents.mapNotNull { it.toObject(WaterEntryDto::class.java) }
//    }

    suspend fun getWaterEntriesByDate(userId: String, date: Date): List<Pair<String, WaterEntryDto>> {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time

        Log.d(TAG, "Запрос воды для userId=$userId, start=$startOfDay, end=$endOfDay")
        try {
            val snapshot = firestore.collection(COLLECTION_WATER)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThan("date", endOfDay)
                .get()
                .await()
            Log.d(TAG, "Получено документов: ${snapshot.documents.size}")
            return snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(WaterEntryDto::class.java)
                if (dto != null) {
                    Log.d(TAG, "Документ id=${doc.id}, amountMl=${dto.amountMl}")
                    Pair(doc.id, dto)
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса: ${e.message}", e)
            if (e.message?.contains("index") == true) {
                Log.e(TAG, "Требуется составной индекс для waterEntries (userId, date)")
            }
            return emptyList()
        }
    }

    suspend fun deleteWaterEntry(entryId: String) {
        Log.d(TAG, "Удаление записи воды с id: $entryId")
        firestore.collection(COLLECTION_WATER).document(entryId).delete().await()
    }

}