package com.example.fooddiary.data.repository.product

import android.util.Log
import com.example.fooddiary.data.datasource.remote.product.OpenFoodFactsSearchApi
import com.example.fooddiary.data.datasource.remote.product.SearchHistoryFirestoreDataSource
import com.example.fooddiary.data.datasource.local.dao.ProductDao
import com.example.fooddiary.data.datasource.local.dao.SearchHistoryDao
import com.example.fooddiary.data.datasource.local.entity.SearchHistoryEntity
import com.example.fooddiary.data.mapper.toDomain
import com.example.fooddiary.data.mapper.toEntity
import com.example.fooddiary.domain.repository.product.IProductRepository
import com.example.fooddiary.domain.model.product.Product
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: OpenFoodFactsSearchApi,
    private val productDao: ProductDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val firestoreHistory: SearchHistoryFirestoreDataSource,
    private val authRepository: IAuthRepository
) : IProductRepository {

    override suspend fun searchProducts(query: String): List<Product> {
        return withContext(Dispatchers.IO) {
            // 1. Ищем локально
            val localProducts = productDao.searchByNameOrBrand(query)
            if (localProducts.isNotEmpty()) {
                localProducts.map { it.toDomain() }
            } else {
                // 2. Загружаем из API
                try {
                    val response = api.searchProducts(query)
                    val entities = response.products.map { it.toEntity() }
                    // Сохраняем в кэш
                    productDao.insertAll(entities)
                    entities.map { it.toDomain() }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
    }

    override suspend fun getSearchHistory(limit: Int): List<String> {
        // Локальная история
        val localHistory = searchHistoryDao.getRecentQueries(limit).map { it.query }

        // Попытка синхронизации с Firestore, если пользователь авторизован
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            try {
                val remoteQueries = firestoreHistory.getAllQueries(currentUser.uid)
                // Сохраняем в локальную БД те, которых ещё нет
                remoteQueries.forEach { query ->
                    searchHistoryDao.insertQuery(SearchHistoryEntity(query = query, timestamp = Date()))
                }
                // Возвращаем обновлённый список (могут быть дубликаты, можно отфильтровать уникальные)
                return searchHistoryDao.getRecentQueries(limit).map { it.query }.distinct()
            } catch (_: Exception) { }
        }
        return localHistory
    }

    override suspend fun addSearchQuery(query: String) {
        // Сохраняем локально
        searchHistoryDao.insertQuery(SearchHistoryEntity(query = query, timestamp = Date()))
        // Отправляем в Firestore
        val currentUser = authRepository.getCurrentUser()
        Log.d("SearchRepo", "Пользователь с id ${currentUser?.uid}: ${currentUser.toString()}")
        if (currentUser != null) {
            try {
                firestoreHistory.addQuery(currentUser.uid, query)
            } catch (_: Exception) { }
        }
    }
}