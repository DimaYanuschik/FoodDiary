package com.example.fooddiary.di.module

import android.content.Context
import androidx.room.Room
import com.example.fooddiary.data.datasource.local.dao.FoodEntryDao
import com.example.fooddiary.data.datasource.local.dao.FoodNutritionDao
import com.example.fooddiary.data.datasource.remote.product.OpenFoodFactsSearchApi
import com.example.fooddiary.data.datasource.remote.product.SearchHistoryFirestoreDataSource
import com.example.fooddiary.data.datasource.local.dao.ProductDao
import com.example.fooddiary.data.datasource.local.dao.SearchHistoryDao
import com.example.fooddiary.data.datasource.local.database.AppDatabase
import com.example.fooddiary.data.datasource.local.foodrecognition.LocalFoodRecognitionDataSource
import com.example.fooddiary.data.repository.FoodRepositoryImpl
import com.example.fooddiary.data.repository.foodrecognition.LocalFoodRecognitionRepositoryImpl
import com.example.fooddiary.data.repository.product.ProductRepositoryImpl
import com.example.fooddiary.data_old.repository.FoodRepository
import com.example.fooddiary.domain.repository.product.IProductRepository
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import com.example.fooddiary.domain.repository.foodrecognition.ILocalFoodRecognitionRepository
import com.example.fooddiary.domain.usecase.foodrecognition.RecognizeFoodLocallyUseCase
import com.example.fooddiary.domain.usecase.product.AddSearchQueryUseCase
import com.example.fooddiary.domain.usecase.product.GetSearchHistoryUseCase
import com.example.fooddiary.domain.usecase.product.SearchProductsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "new_food_diary.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun provideFoodEntryDao(db: AppDatabase): FoodEntryDao = db.foodEntryDao()

    @Provides
    @Singleton
    fun provideFoodRepositoryImpl(
        foodEntryDao: FoodEntryDao,
        firestoreFoodRepository: FoodRepository
    ): FoodRepositoryImpl {
        return FoodRepositoryImpl(foodEntryDao, firestoreFoodRepository)
    }

    // Провайдер OkHttpClient для Retrofit
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "FoodDiary - Android - Version 1.0")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsSearchApi(okHttpClient: OkHttpClient): OpenFoodFactsSearchApi {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsSearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        api: OpenFoodFactsSearchApi,
        productDao: ProductDao,
        searchHistoryDao: SearchHistoryDao,
        firestoreHistory: SearchHistoryFirestoreDataSource,
        authRepository: IAuthRepository
    ): IProductRepository {
        return ProductRepositoryImpl(api, productDao, searchHistoryDao, firestoreHistory, authRepository)
    }


    @Provides
    fun provideFoodNutritionDao(db: AppDatabase): FoodNutritionDao = db.foodNutritionDao()

    @Provides
    @Singleton
    fun provideLocalFoodRecognitionDataSource(@ApplicationContext context: Context): LocalFoodRecognitionDataSource {
        return LocalFoodRecognitionDataSource(context)
    }

    @Provides
    @Singleton
    fun provideLocalFoodRecognitionRepository(
        dataSource: LocalFoodRecognitionDataSource,
        nutritionDao: FoodNutritionDao,
        @ApplicationContext context: Context
    ): ILocalFoodRecognitionRepository {
        return LocalFoodRecognitionRepositoryImpl(dataSource, nutritionDao, context)
    }

    @Provides
    fun provideRecognizeFoodLocallyUseCase(
        repository: ILocalFoodRecognitionRepository
    ): RecognizeFoodLocallyUseCase {
        return RecognizeFoodLocallyUseCase(repository)
    }


    @Provides
    fun provideSearchProductsUseCase(repo: IProductRepository): SearchProductsUseCase {
        return SearchProductsUseCase(repo)
    }

    @Provides
    fun provideGetSearchHistoryUseCase(repo: IProductRepository): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(repo)
    }

    @Provides
    fun provideAddSearchQueryUseCase(repo: IProductRepository): AddSearchQueryUseCase {
        return AddSearchQueryUseCase(repo)
    }
}