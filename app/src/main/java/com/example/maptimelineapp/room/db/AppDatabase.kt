package com.example.maptimelineapp.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.maptimelineapp.room.dao.UserLocationDao
import com.example.maptimelineapp.room.models.DateConverter
import com.example.maptimelineapp.room.models.UserLocation

@Database(entities = [UserLocation::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val userLocationDao: UserLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "location-database"
                    ).allowMainThreadQueries()
                    .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}