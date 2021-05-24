package com.example.maptimelineapp.datasource

import androidx.annotation.WorkerThread
import com.example.maptimelineapp.datasource.room.dao.UserLocationDao
import com.example.maptimelineapp.datasource.room.models.UserLocation
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

class UserLocationRepository(private val userLocationDao: UserLocationDao) {

    val userLocationsList: Flow<List<UserLocation>> = userLocationDao.getAllUserLocations()

    fun userLocationByDateRange(startDate: OffsetDateTime?, endDate: OffsetDateTime?): Flow<List<UserLocation>> {
        return userLocationDao.loadUserLocationsByDateRange(startDate, endDate)
    }

    @WorkerThread
    suspend fun insert(userLocation: UserLocation){
        userLocationDao.insertUserLocation(userLocation)
    }
}