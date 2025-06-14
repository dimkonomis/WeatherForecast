package com.app.weatherforecast

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.weatherforecast.core.navigation.Back
import com.app.weatherforecast.core.navigation.LocationRoute
import com.app.weatherforecast.core.navigation.Pop
import com.app.weatherforecast.core.navigation.Route
import com.app.weatherforecast.core.navigation.RouteViewModel
import com.app.weatherforecast.core.navigation.WeatherRoute
import com.app.weatherforecast.core.ui.theme.AppTheme
import com.app.weatherforecast.feature.location.presentation.LocationScreen
import com.app.weatherforecast.feature.weather.presentation.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val mainViewModel: MainViewModel = hiltViewModel()

                val startDestination: Route = runBlocking {
                    mainViewModel.startDestination()
                }

                when (val destination = startDestination) {
                    is LocationRoute -> Content(destination)
                    is WeatherRoute -> Content(destination)
                    else -> Unit
                }
            }
        }
    }

    @Composable
    private fun Content(startDestination: Route) {
        val navController = rememberNavController()

        val routeViewModel: RouteViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            routeViewModel.nextRoute
                .onEach { route ->
                    navController.handleRoutes(route)
                }
                .launchIn(this)
        }

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                composable<LocationRoute> {
                    LocationScreen()
                }
                composable<WeatherRoute> {
                    WeatherScreen()
                }
            }
        }
    }

    private fun NavController.handleRoutes(route: Route) {
        when (route) {
            is Back -> {
                previousBackStackEntry
                    ?.savedStateHandle
                    ?.run {
                        route.arguments.forEach { (key, value) ->
                            set(key, value)
                        }
                    }
                popBackStack()
            }

            is Pop -> popBackStack(route.route, route.inclusive)
            else -> navigate(route) {
                kotlin.runCatching {
                    val popupTo = route.popupTo
                    if (popupTo != null) popUpTo(popupTo.route) {
                        inclusive = popupTo.inclusive
                    }
                }.onFailure {
                    Timber.e(it, "Failed to popupTo: ${route.popupTo}")
                }
            }
        }
    }
}
