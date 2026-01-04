package com.example.fooddiary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}