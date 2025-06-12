package com.app.weatherforecast.feature.location.data

import android.content.Context
import android.location.Location
import app.cash.turbine.test
import com.app.weatherforecast.core.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationProviderTest {

    private val context: Context = mockk()
    private val locationClient: FusedLocationProviderClient = mockk()
    private lateinit var locationProvider: LocationProvider

    @Before
    fun setup() {
        mockkStatic("com.app.weatherforecast.core.utils.ContextKt")
        locationProvider = LocationProvider(context, locationClient)
    }

    @Test
    fun `should emit location when permission granted and location returned`() = runTest {
        // Given
        every { any<Context>().hasLocationPermission() } returns true

        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns 1.23
        every { mockLocation.longitude } returns 4.56

        val slot = slot<OnSuccessListener<Location?>>()
        val task = mockk<Task<Location>>()
        every { locationClient.lastLocation } returns task

        every {
            task.addOnSuccessListener(capture(slot))
        } answers {
            slot.captured.onSuccess(mockLocation)
            task
        }

        // When
        locationProvider.location().test {
            // Then
            val item = awaitItem()
            assert(item.lat == 1.23)
            assert(item.long == 4.56)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should throw LocationPermissionException when no location permission`() = runTest {
        // Given
        every { context.hasLocationPermission() } returns false

        // When
        locationProvider.location().test {
            // Then
            val error = awaitError()
            assert(error.message == "Location permission is not granted")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not emit when location is null`() = runTest {
        // Given
        every { any<Context>().hasLocationPermission() } returns true

        val slot = slot<OnSuccessListener<Location?>>()
        val task = mockk<Task<Location>>()
        every { locationClient.lastLocation } returns task

        every {
            task.addOnSuccessListener(capture(slot))
        } answers {
            slot.captured.onSuccess(null)
            task
        }

        // When
        locationProvider.location().test {
            // Then
            expectNoEvents() // nothing emitted because location is null
            cancelAndIgnoreRemainingEvents()
        }
    }

}