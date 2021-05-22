package com.example.maptimelineapp.screens

import androidx.appcompat.app.AppCompatActivity
import com.example.maptimelineapp.room.dao.UserLocationDao
import com.example.maptimelineapp.room.db.AppDatabase

open class BaseActivity : AppCompatActivity() {

    open fun userDataDb(): UserLocationDao {
        return AppDatabase.getInstance(this).userLocationDao
    }
}