package com.example.maptimelineapp.screens

import androidx.lifecycle.*
import com.example.maptimelineapp.datasource.UserLocationRepository
import com.example.maptimelineapp.datasource.room.models.UserLocation
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class MainActivityViewModel(private val repository: UserLocationRepository) : ViewModel() {

    val userLocationList: LiveData<List<UserLocation>> = repository.userLocationsList.asLiveData()

    fun getLocationsByDateRange(startDate: OffsetDateTime?, endDate: OffsetDateTime?): LiveData<List<UserLocation>>{
        return repository.userLocationByDateRange(startDate, endDate).asLiveData()
    }

    fun insert(userLocation: UserLocation) = viewModelScope.launch {
        repository.insert(userLocation)
    }
}

class MainActivityViewModelFactory(private val repository: UserLocationRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}