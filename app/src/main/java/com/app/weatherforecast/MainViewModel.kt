package com.app.weatherforecast

import androidx.lifecycle.ViewModel
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.navigation.LocationRoute
import com.app.weatherforecast.core.navigation.LocationRoute.Mode
import com.app.weatherforecast.core.navigation.Route
import com.app.weatherforecast.core.navigation.WeatherRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    suspend fun startDestination(): Route {
        val location = locationRepository
            .selected
            .first()

        return when (location) {
            is Location.Available -> WeatherRoute()
            Location.NotAvailable -> LocationRoute(Mode.Create)
        }
    }

}