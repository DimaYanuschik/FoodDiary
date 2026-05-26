package com.example.fooddiary.di.module

import com.example.fooddiary.data.datasource.remote.foodrecognition.GroqFoodRecognitionDataSource
import com.example.fooddiary.data.repository.foodrecognition.FoodRecognitionRepositoryImpl
import com.example.fooddiary.domain.repository.foodrecognition.IFoodRecognitionRepository
import com.example.fooddiary.domain.usecase.foodrecognition.RecognizeFoodUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FoodRecognitionModule {

    @Provides
    @Singleton
    fun provideGroqFoodRecognitionDataSource(okHttpClient: OkHttpClient): GroqFoodRecognitionDataSource {
        return GroqFoodRecognitionDataSource(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideFoodRecognitionRepository(dataSource: GroqFoodRecognitionDataSource): IFoodRecognitionRepository {
        return FoodRecognitionRepositoryImpl(dataSource)
    }

    @Provides
    fun provideRecognizeFoodUseCase(repository: IFoodRecognitionRepository): RecognizeFoodUseCase {
        return RecognizeFoodUseCase(repository)
    }
}