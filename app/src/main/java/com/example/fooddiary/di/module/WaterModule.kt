package com.example.fooddiary.di.module

import com.example.fooddiary.data.datasource.remote.water.WaterFirestoreDataSource
import com.example.fooddiary.data.repository.water.WaterRepositoryImpl
import com.example.fooddiary.data_old.repository.UserProfileRepository
import com.example.fooddiary.domain.repository.water.IWaterRepository
import com.example.fooddiary.domain.usecase.water.AddWaterUseCase
import com.example.fooddiary.domain.usecase.water.DeleteWaterEntryUseCase
import com.example.fooddiary.domain.usecase.water.GetWaterGoalUseCase
import com.example.fooddiary.domain.usecase.water.GetWaterProgressUseCase
import com.example.fooddiary.domain.usecase.water.SetWaterGoalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WaterModule {
    @Provides
    @Singleton
    fun provideWaterRepository(
        dataSource: WaterFirestoreDataSource,
        userProfileRepository: UserProfileRepository
    ): IWaterRepository {
        return WaterRepositoryImpl(dataSource, userProfileRepository)
    }

    @Provides
    fun provideAddWaterUseCase(repo: IWaterRepository): AddWaterUseCase = AddWaterUseCase(repo)

    @Provides
    fun provideGetWaterProgressUseCase(repo: IWaterRepository): GetWaterProgressUseCase = GetWaterProgressUseCase(repo)

    @Provides
    fun provideDeleteWaterEntryUseCase(repo: IWaterRepository): DeleteWaterEntryUseCase = DeleteWaterEntryUseCase(repo)

    @Provides
    fun provideGetWaterGoalUseCase(repo: IWaterRepository): GetWaterGoalUseCase = GetWaterGoalUseCase(repo)

    @Provides
    fun provideSetWaterGoalUseCase(repo: IWaterRepository): SetWaterGoalUseCase = SetWaterGoalUseCase(repo)
}