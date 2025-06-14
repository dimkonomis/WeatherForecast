package com.app.weatherforecast

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.navigation.LocationRoute
import com.app.weatherforecast.core.navigation.WeatherRoute
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `startDestination with NotAvailable location `() = runTest {
        // Given
        val locationRepository = mockk<LocationRepository> {
            coEvery { selected } returns flowOf(Location.NotAvailable)
        }
        val viewModel = MainViewModel(locationRepository)

        // When
        val route = viewModel.startDestination()

        // Then
        assert(route is LocationRoute)
    }

    @Test
    fun `startDestination with Available location `() = runTest {
        // Given
        val locationRepository = mockk<LocationRepository> {
            coEvery { selected } returns flowOf(Location.Available("Test Location"))
        }
        val viewModel = MainViewModel(locationRepository)

        // When
        val route = viewModel.startDestination()

        // Then
        assert(route is WeatherRoute)
    }

}
