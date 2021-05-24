package com.example.maptimelineapp.datasource.room.dao

import androidx.room.*
import com.example.maptimelineapp.datasource.room.models.UserLocation
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface UserLocationDao {
    @Query("SELECT * FROM userLocation ORDER BY DATE DESC")
    fun getAllUserLocations(): Flow<List<UserLocation>>

    @Query("SELECT * FROM userLocation WHERE DATE BETWEEN :startDate AND :endDate ORDER BY DATE DESC")
    fun loadUserLocationsByDateRange(startDate: OffsetDateTime?, endDate: OffsetDateTime?): Flow<List<UserLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUserLocation(usersLocation: Array<UserLocation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserLocation(usersLocation: UserLocation)

    @Delete
    fun delete(user: UserLocation)

    @Query("DELETE FROM userLocation")
    suspend fun deleteAll()
}