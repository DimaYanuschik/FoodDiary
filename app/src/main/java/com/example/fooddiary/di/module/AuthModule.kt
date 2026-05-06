// di/module/AuthModule.kt
package com.example.fooddiary.di.module

import com.example.fooddiary.domain.repository.auth.IAuthRepository
import com.example.fooddiary.data.repository.auth.AuthRepositoryImpl
import com.example.fooddiary.data.datasource.remote.auth.FirebaseAuthDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(): FirebaseAuthDataSource {
        return FirebaseAuthDataSource()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: FirebaseAuthDataSource): IAuthRepository {
        return AuthRepositoryImpl(dataSource)
    }
}