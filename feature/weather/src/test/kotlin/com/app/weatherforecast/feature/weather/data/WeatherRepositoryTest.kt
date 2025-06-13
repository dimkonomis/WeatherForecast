package com.app.weatherforecast.feature.weather.data

import com.app.weatherforecast.core.test.SampleData
import com.app.weatherforecast.core.test.createJsonResponse
import com.app.weatherforecast.core.test.createTestApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import org.junit.Test

class WeatherRepositoryTest {

    @Test
    fun `weather returns success response`() = runTest {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val api = createTestApi<WeatherApi> {
            it.request().createJsonResponse(200, Success.readText())
        }

        // When
        val response = WeatherRepository(api).fetch(lat, lon, unit)

        // Then
        assert(response.current.temperature == 32.92)
        assert(response.current.feelsLike == 32.52)
        assert(response.hourly.size == 48)
        assert(response.daily.size == 8)
    }


    @Test
    fun `events returns bad request`() = runTest {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val api = createTestApi<WeatherApi> {
            it.request().createJsonResponse(400, Error.readText())
        }

        // When
        val response = runCatching {
            WeatherRepository(api).fetch(lat, lon, unit)
        }

        // Then
        assert(response.isFailure)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `events returns success response v2 with unexpected keys`() = runTest {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val api = createTestApi<WeatherApi> {
            it.request().createJsonResponse(200, SuccessV2.readText())
        }

        // When
        val response = runCatching {
            WeatherRepository(api).fetch(lat, lon, unit)
        }

        // When
        assert(response.isFailure)
        assert(response.exceptionOrNull() is MissingFieldException)
    }

    data object Success : SampleData {
        override val code: Int = 200
        override val file: String = "response_200.json"
    }

    data object SuccessV2 : SampleData {
        override val code: Int = 200
        override val file: String = "response_200_v2.json"
    }

    data object Error : SampleData {
        override val code: Int = 400
        override val file: String = "response_400.json"
    }

}