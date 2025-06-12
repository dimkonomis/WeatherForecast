package com.app.weatherforecast.feature.location.data

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.core.utils.SdkProvider
import com.app.weatherforecast.feature.location.data.LocationDecoder.DecodeResult.AddressNotFound
import com.app.weatherforecast.feature.location.data.LocationDecoder.DecodeResult.Success
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocationDecoder @Inject constructor(
    private val geocoder: Geocoder,
    private val sdkProvider: SdkProvider
) {

    fun decodeLocation(lat: Double, long: Double): Flow<DecodeResult> = callbackFlow {

        fun handleAddresses(addresses: List<Address>) {
            when {
                addresses.isNotEmpty() -> addresses.first()
                    .let { address ->
                        val location = Location.Available(
                            name = address.locality,
                            lat = lat,
                            long = long,
                        )
                        trySend(Success(location))
                    }
                else -> trySend(AddressNotFound)
            }
        }

        if (sdkProvider.sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            @Suppress("NewApi")
            geocoder.getFromLocation(lat, long, 1) { addresses -> handleAddresses(addresses) }
        } else {
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (addresses != null) {
                handleAddresses(addresses)
            } else {
                trySend(AddressNotFound)
            }
        }

        awaitClose()
    }

    sealed interface DecodeResult {
        data class Success(val location: Location) : DecodeResult
        data object AddressNotFound : DecodeResult
    }

}