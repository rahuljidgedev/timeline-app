package com.example.maptimelineapp.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class UserLocation(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "locationName") val locationName: String?,
    @ColumnInfo(name = "locationDec") val locationDesc: String?,
    @ColumnInfo(name = "date") val date: Date?
) {

    override fun toString(): String {
        return "UserLocation{" +
                "id='" + id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", locationName='" + locationName + '\'' +
                ", locationDesc='" + locationDesc + '\'' +
                ", date='" + date?.time + '\'' +
                '}'
    }
}