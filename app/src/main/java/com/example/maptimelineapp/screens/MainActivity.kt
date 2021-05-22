package com.example.maptimelineapp.screens

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import com.example.maptimelineapp.R
import com.example.maptimelineapp.room.models.DateConverter
import com.example.maptimelineapp.room.models.UserLocation
import java.util.*
import kotlin.random.Random


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameObserver: Observer<List<UserLocation?>> =
            Observer<List<UserLocation?>> { newLocation ->
                Log.e("\napple",newLocation.toString())
            }

        userDataDb().getAllUserLocations().observe(this, nameObserver)

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                userDataDb().insertUserLocation(
                    UserLocation(
                        Random.nextInt(),
                        Random.nextDouble(),
                        Random.nextDouble(),
            "Name ${Random.nextInt()}",
            "Description ${Random.nextInt()}",
                        DateConverter().fromTimeStamp(Date().time),
                    )
                )
            }

            override fun onFinish() {
                Log.i("apple","done!")
            }
        }.start()
    }
}