package com.example.maptimelineapp.screens

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.maptimelineapp.R
import com.example.maptimelineapp.UserNavigationApplication
import com.example.maptimelineapp.backgroundschedular.AutoLocationCollection
import com.example.maptimelineapp.datasource.room.models.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity(), UserLocationAdapter.OnItemClickListener{
    private lateinit var adapter : UserLocationAdapter
    private lateinit var googleMap : GoogleMap
    private var selectedMarker : Marker? = null
    private lateinit var pref: SharedPreferences

    private val locationViewModel : MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as UserNavigationApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = UserLocationAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        pref = this.getPreferences(Context.MODE_PRIVATE)

        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMaps ->
            googleMaps.uiSettings.isZoomControlsEnabled = true
            googleMaps.uiSettings.isZoomGesturesEnabled = true
            googleMaps.uiSettings.isCompassEnabled = true
            googleMaps.setMinZoomPreference(3.0f)
            googleMaps.setMaxZoomPreference(14.0f)
            googleMap = googleMaps

            googleMap.setOnMapClickListener { it ->
                selectedMarker = googleMap
                    .addMarker(MarkerOptions()
                    .position(it)
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_add_location)))

                /**Add location by tapping on a map marker*/
                googleMap.setOnMarkerClickListener {
                    if (it  == selectedMarker){
                        selectedMarker?.remove()
                        val geoCoder= Geocoder(this, Locale.getDefault())
                        val add: List<Address> = geoCoder
                            .getFromLocation(it.position.latitude, it.position.longitude, 1)
                        val name = if(add.isNotEmpty())
                                        add[0].getAddressLine(0).toString()
                                else ""
                        locationViewModel.insert(
                            UserLocation(
                                Random.nextInt(),
                                it.position.latitude,
                                it.position.longitude,
                                name,
                                "Description",
                                OffsetDateTime.now()
                            )
                        )
                        selectedMarker = null
                    }
                    true
                }
            }
        }


        /**All locations*/
        locationViewModel.userLocationList.observe(this){ location ->
            location?.let {
                adapter.submitList(location)
                if(googleMap != null){
                    addMarkers(location)
                }
            }
        }

        /**Locations by date range
         * To get location of today set  startDate = current day -1 & endDate = currentDate*/
        /*locationViewModel.getLocationsByDateRange(OffsetDateTime.now().minusDays(1),
            OffsetDateTime.now()).observe(this){ location ->
            location?.let {
                adapter.submitList(location)
            }
        }*/


        findViewById<SwitchCompat>(R.id.locationLogging).isChecked = pref.getBoolean("auto-location-logging", false)
        findViewById<SwitchCompat>(R.id.locationLogging).setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("auto-location-logging", isChecked).apply()
            /** Auto location is on Start the Worker to log location every 15 minutes delay*/
            if(isChecked){
                val loggingReq =
                    PeriodicWorkRequestBuilder<AutoLocationCollection>(15, TimeUnit.MINUTES)
                        .addTag("Location Collection")
                        .build()
                WorkManager
                    .getInstance(applicationContext)
                    .enqueue(loggingReq)
            }else{
                /** Turn of if any ongoing Auto location logging is running*/
                try {
                    WorkManager
                        .getInstance(applicationContext)
                        .getWorkInfosByTag("Location Collection")
                        .cancel(true)
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onItemClick(item: UserLocation?) {
        val bounds = LatLngBounds(
            LatLng(item?.latitude!!, item.longitude!!),
            LatLng(item.latitude, item.longitude)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
    }

    private fun addMarkers(locations: List<UserLocation>) {
        locations.forEach {
            googleMap.addMarker(
                MarkerOptions()
                    .title(it.locationName)
                    .position(LatLng(it.latitude!!, it.longitude!!))
            )
        }
    }

    private fun Context.bitmapDescriptorFromVector(vectorResId:Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        vectorDrawable.draw(Canvas(bitmap))
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}