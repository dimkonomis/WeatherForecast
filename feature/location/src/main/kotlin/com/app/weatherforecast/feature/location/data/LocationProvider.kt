package com.app.weatherforecast.feature.location.data

import android.content.Context
import com.app.weatherforecast.core.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient
) {

    private val hasLocationPermission: Boolean
        get() = context.hasLocationPermission()

    /**
     * Provides the last known location as a flow of latitude and longitude pairs.
     * This method requires the ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission.
     */
    @Throws(LocationPermissionException::class)
    fun location(): Flow<LocationResult> = callbackFlow {
        if (hasLocationPermission.not()) {
            throw LocationPermissionException()
        }

        Timber.d("Requesting location updates")

        val successListener = object : OnSuccessListener<android.location.Location?> {
            override fun onSuccess(location: android.location.Location?) {
                if (location != null) {
                    trySend(LocationResult(location.latitude, location.longitude))
                } else {
                    Timber.w("Location is null")
                }
            }

        }

        /**
         * We suppress the lint warning for missing permission because the permission check is done
         * before accessing location.
         */
        @Suppress("MissingPermission")
        locationProviderClient.lastLocation.addOnSuccessListener(successListener)

        awaitClose()
    }

    data class LocationResult(val lat: Double, val long: Double)

    data class LocationPermissionException(
        override val message: String = "Location permission is not granted"
    ) : java.lang.IllegalStateException(message)

}