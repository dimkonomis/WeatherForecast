package com.app.weatherforecast.feature.weather.presentation

import app.cash.turbine.test
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.test.LocaleRule
import com.app.weatherforecast.core.utils.DateProvider
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.weather.domain.Weather
import com.app.weatherforecast.feature.weather.domain.WeatherState
import com.app.weatherforecast.feature.weather.domain.WeatherUseCase
import com.app.weatherforecast.feature.weather.sampleWeatherResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val localeRule = LocaleRule()

    private val dispatcher = UnconfinedTestDispatcher()
    private val dateProvider = object : DateProvider {
        override val now: LocalDateTime = LocalDateTime.of(2025, 6, 10, 13, 0)
    }
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = dispatcher
        override val ui = dispatcher
        override val default = dispatcher
    }

    @Test
    fun `init calls useCase`() = runTest(dispatcher) {
        // Given
        val useCase = mockk<WeatherUseCase> {
            coEvery { get() } returns Unit
            every { state } returns flowOf(WeatherState(AsyncResult.Loading))
        }
        WeatherViewModel(useCase, dateProvider, dispatcherProvider)

        // Then
        coVerify(exactly = 1) { useCase.get() }
    }

    @Test
    fun `retry calls useCase`() = runTest(dispatcher) {
        // Given
        val useCase = mockk<WeatherUseCase> {
            coEvery { get() } returns Unit
            every { state } returns flowOf(WeatherState(AsyncResult.Loading))
        }
        val viewModel = WeatherViewModel(useCase, dateProvider, dispatcherProvider)

        // When
        viewModel.retry()

        // Then
        // 1st call is from init, 2nd is from retry
        coVerify(exactly = 2) { useCase.get() }
    }

    @Test
    fun `uiState emits Error after domain AsyncResult Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Domain Error")
        val useCase = mockk<WeatherUseCase> {
            coEvery { get() } returns Unit
            every { state } returns flowOf(WeatherState(AsyncResult.Failure(error)))
        }
        val viewModel = WeatherViewModel(useCase, dateProvider, dispatcherProvider)

        // When
        viewModel.uiState.test {
            // Then
            assert(awaitItem() is WeatherUiState.Loading)
            assert(awaitItem() is WeatherUiState.Error)
        }
    }

    @Test
    fun `uiState emits Success after domain AsyncResult Success`() = runTest(dispatcher) {
        // Given
        val weather = Weather("Test", sampleWeatherResponse)
        val useCase = mockk<WeatherUseCase> {
            coEvery { get() } returns Unit
            every { state } returns flowOf(WeatherState(AsyncResult.Success(weather)))
        }
        val viewModel = WeatherViewModel(useCase, dateProvider, dispatcherProvider)

        // When
        viewModel.uiState.test {
            // Then
            assert(awaitItem() is WeatherUiState.Loading)

            val value = awaitItem()
            assert(value is WeatherUiState.Success)
            val success = (value as WeatherUiState.Success)
            assert(success.current.temperature == 22.5)
            assert(success.current.summary == "Clear")
            assert(value.today.size == 2)
            assert(value.today.first().dateTime == "2 PM")
            assert(value.forecast.size == 2)
            assert(value.forecast.first().dateTime == "Wed")
        }
    }

}