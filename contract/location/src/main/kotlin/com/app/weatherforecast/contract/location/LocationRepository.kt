package com.app.weatherforecast.contract.location

import com.app.weatherforecast.contract.location.Location.Available
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    val selected: Flow<Location>

    suspend fun update(location: Available)

}