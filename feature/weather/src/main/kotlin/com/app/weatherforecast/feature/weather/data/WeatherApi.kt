package com.app.weatherforecast.feature.weather.data

import retrofit2.http.GET
import retrofit2.http.Query

private const val VERSION = "3.0"
private const val ENDPOINT = "$VERSION/onecall"
private const val LAT_QUERY = "lat"
private const val LON_QUERY = "lon"
private const val UNITS_QUERY = "units"

internal interface WeatherApi {

    /**
     * Fetches the weather data for a specific location at a given time.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @param unit The unit of measurement for the weather data.
     * @return A [WeatherResponse] containing the weather data.
     */
    @GET(ENDPOINT)
    suspend fun weather(
        @Query(LAT_QUERY) lat: Double,
        @Query(LON_QUERY) lon: Double,
        @Query(UNITS_QUERY) unit: String
    ): WeatherResponse

}