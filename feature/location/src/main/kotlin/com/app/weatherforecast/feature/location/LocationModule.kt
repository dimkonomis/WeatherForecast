package com.app.weatherforecast.feature.location

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.location.data.LocationRepositoryImpl
import com.app.weatherforecast.feature.location.data.LocationSerializer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LocationModule {

    @Binds
    @Singleton
    fun bindLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository

    companion object {

        @Provides
        @Singleton
        fun provideDataStore(
            @ApplicationContext context: Context,
            locationSerializer: LocationSerializer,
            dispatcherProvider: DispatcherProvider
        ): DataStore<Location> {
            return DataStoreFactory.create(
                serializer = locationSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = {
                        Location.NotAvailable
                    }
                ),
                scope = CoroutineScope(dispatcherProvider.io + SupervisorJob()),
                produceFile = { context.dataStoreFile(DATASTORE_FILE_NAME) }
            )
        }

        @Provides
        @Singleton
        fun provideGeoDecoder(
            @ApplicationContext context: Context
        ): Geocoder {
            return Geocoder(context, Locale.getDefault())
        }

        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }

        private const val DATASTORE_FILE_NAME = "user_location"

    }

}