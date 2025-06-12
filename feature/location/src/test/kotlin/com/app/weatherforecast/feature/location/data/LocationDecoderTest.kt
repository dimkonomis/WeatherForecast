package com.app.weatherforecast.feature.location.data

import android.location.Address
import android.location.Geocoder
import android.os.Build
import app.cash.turbine.test
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.core.utils.SdkProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationDecoderTest {

    private val geocoder: Geocoder = mockk()
    private val sdkProvider: SdkProvider = mockk()
    private lateinit var decoder: LocationDecoder

    @Before
    fun setup() {
        decoder = LocationDecoder(geocoder, sdkProvider)
    }

    @Test
    fun `should emit Success when address is returned (TIRAMISU and above)`() = runTest {
        // Given
        val address = mockk<Address> { every { locality } returns "Madrid" }
        val slot = slot<Geocoder.GeocodeListener>()
        every { sdkProvider.sdkInt } returns Build.VERSION_CODES.TIRAMISU
        every {
            geocoder.getFromLocation(any(), any(), any(), capture(slot))
        } answers {
            slot.captured.onGeocode(listOf(address))
        }

        // When
        decoder.decodeLocation(37.98, 23.72).test {
            // Then
            val item = awaitItem()
            assert(item is LocationDecoder.DecodeResult.Success)
            val location = (item as LocationDecoder.DecodeResult.Success)
                .location as Location.Available
            assert("Madrid" == location.name)
            assert(37.98 == location.lat)
            assert(23.72 == location.long)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit AddressNotFound when empty list is returned (TIRAMISU and above)`() = runTest {
        // Given
        val slot = slot<Geocoder.GeocodeListener>()
        every { sdkProvider.sdkInt } returns Build.VERSION_CODES.TIRAMISU
        every {
            geocoder.getFromLocation(any(), any(), any(), capture(slot))
        } answers {
            slot.captured.onGeocode(emptyList<Address>())
        }

        // When
        decoder.decodeLocation(0.0, 0.0).test {
            // Then
            assert(awaitItem() is LocationDecoder.DecodeResult.AddressNotFound)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Success when address is returned (pre-TIRAMISU)`() = runTest {
        // Given
        val address = mockk<Address> { every { locality } returns "Paris" }
        every { sdkProvider.sdkInt } returns Build.VERSION_CODES.P
        every {
            geocoder.getFromLocation(any(), any(), eq(1))
        } returns listOf(address)

        // When
        decoder.decodeLocation(48.85, 2.35).test {
            // Then
            val item = awaitItem()
            assert(item is LocationDecoder.DecodeResult.Success)
            val location = (item as LocationDecoder.DecodeResult.Success)
                .location as Location.Available
            assert("Paris" == location.name)
            assert(48.85 == location.lat)
            assert(2.35 == location.long)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit AddressNotFound when empty list is returned (pre-TIRAMISU)`() = runTest {
        // Given
        every { sdkProvider.sdkInt } returns Build.VERSION_CODES.P
        every {
            geocoder.getFromLocation(any(), any(), eq(1))
        } returns emptyList<Address>()

        // When
        decoder.decodeLocation(0.0, 0.0).test {
            // Then
            assert(awaitItem() is LocationDecoder.DecodeResult.AddressNotFound)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit AddressNotFound when returns null (pre-TIRAMISU)`() = runTest {
        // Given
        every { sdkProvider.sdkInt } returns Build.VERSION_CODES.P
        every {
            geocoder.getFromLocation(any(), any(), eq(1))
        } returns null

        // When
        decoder.decodeLocation(0.0, 0.0).test {
            // Then
            assert(awaitItem() is LocationDecoder.DecodeResult.AddressNotFound)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
