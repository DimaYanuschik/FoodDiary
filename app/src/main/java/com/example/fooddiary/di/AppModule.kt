package com.example.fooddiary.di

import android.content.Context
import com.example.fooddiary.data_old.api.ApiClient
import com.example.fooddiary.data_old.database.BarcodeDatabase
import com.example.fooddiary.data_old.repository.BarcodeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBarcodeDatabase(@ApplicationContext context: Context): BarcodeDatabase {
        return BarcodeDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
        return ApiClient()
    }

    @Provides
    @Singleton
    fun provideBarcodeRepository(
        @ApplicationContext context: Context,
        apiClient: ApiClient,
        barcodeDatabase: BarcodeDatabase
    ): BarcodeRepository {
        return BarcodeRepository(context, apiClient, barcodeDatabase)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}