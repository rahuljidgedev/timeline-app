package com.example.maptimelineapp.backgroundschedular

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.maptimelineapp.UserNavigationApplication
import com.example.maptimelineapp.datasource.room.models.UserLocation
import com.google.android.gms.location.*
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

class AutoLocationCollection(var appContext: Context, workParams:WorkerParameters) :
    Worker(appContext, workParams){

    var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest =  LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime= 60
    }

    override fun doWork(): Result {
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(appContext)

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return Result.failure()
        }
        fusedLocationProvider?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        return Result.success()
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()

                val geoCoder= Geocoder(appContext, Locale.getDefault())
                val add: List<Address> = geoCoder
                    .getFromLocation(location.latitude, location.longitude, 1)
                val name = if(add.isNotEmpty())
                    add[0].getAddressLine(0).toString()
                else ""

                (appContext.applicationContext as UserNavigationApplication)
                    .database.userLocationDao
                    .insertUserLocation(
                        UserLocation(
                            Random.nextInt(),
                            location.latitude,
                            location.longitude,
                            name,
                            "Desc",
                            OffsetDateTime.now()
                        )
                    )

                fusedLocationProvider?.removeLocationUpdates(this)
                Log.i("Location Update", name)
            }
        }
    }
}