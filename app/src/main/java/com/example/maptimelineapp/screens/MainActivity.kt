package com.example.maptimelineapp.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maptimelineapp.R
import com.example.maptimelineapp.UserNavigationApplication
import com.example.maptimelineapp.datasource.room.models.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.OffsetDateTime
import kotlin.random.Random


class MainActivity : AppCompatActivity(), UserLocationAdapter.OnItemClickListener{
    private lateinit var adapter : UserLocationAdapter
    private lateinit var googleMap : GoogleMap
    private val locationViewModel : MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as UserNavigationApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = UserLocationAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMaps ->
            googleMaps.uiSettings.isZoomControlsEnabled = true
            googleMaps.uiSettings.isZoomGesturesEnabled = true
            googleMaps.uiSettings.isCompassEnabled = true
            googleMaps.setMinZoomPreference(6.0f)
            googleMaps.setMaxZoomPreference(14.0f)
            googleMap=googleMaps
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

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { it ->
            locationViewModel.insert(
                UserLocation(
                    Random.nextInt(),
                    24.618393,
                    88.024338,
                    "Suti, West Bengal, India",
                    "Description ${Random.nextInt()}",
                    OffsetDateTime.now().minusDays(1),/*.plusDays(1)*/
                )
            )
        }
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

    override fun onItemClick(item: UserLocation?) {
        val bounds = LatLngBounds(
            LatLng(item?.latitude!!, item.longitude!!),
            LatLng(item.latitude, item.longitude)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
    }
}