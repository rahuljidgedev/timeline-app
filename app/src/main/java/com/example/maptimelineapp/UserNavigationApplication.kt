package com.example.maptimelineapp

import android.app.Application
import com.example.maptimelineapp.datasource.UserLocationRepository
import com.example.maptimelineapp.datasource.room.db.AppDatabase

class UserNavigationApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { UserLocationRepository(database.userLocationDao)}
}