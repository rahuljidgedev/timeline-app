package com.example.maptimelineapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.maptimelineapp.room.models.UserLocation
import java.util.*

@Dao
interface UserLocationDao {
    @Query("SELECT * FROM userLocation")
    fun getAllUserLocations(): LiveData<List<UserLocation>>

    @Query("SELECT * FROM userLocation WHERE date IN (:date)")
    fun loadUserLocationsByDate(date: Date?): LiveData<List<UserLocation>>

    @Query("SELECT * FROM userLocation WHERE date BETWEEN :startDate AND :endDate ORDER BY DATE DESC")
    fun loadUserLocationsByDateRange(startDate: Date?, endDate: Date?): LiveData<List<UserLocation>>

    @Insert
    fun insertAllUserLocation(usersLocation: Array<UserLocation>)

    @Insert
    fun insertUserLocation(usersLocation: UserLocation)

    @Delete
    fun delete(user: UserLocation)
}