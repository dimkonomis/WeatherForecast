package com.app.weatherforecast.feature.location.data

import androidx.datastore.core.DataStore
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocationRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Location>
): LocationRepository {

    override val selected: Flow<Location> = dataStore.data

    override suspend fun update(location: Location.Available) {
        dataStore.updateData { location }
    }

}