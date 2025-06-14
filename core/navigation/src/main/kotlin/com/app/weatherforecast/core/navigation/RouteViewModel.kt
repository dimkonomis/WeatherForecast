package com.app.weatherforecast.core.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class RouteViewModel : ViewModel() {

    private val _route = Channel<Route>()
    val nextRoute: Flow<Route> = _route.receiveAsFlow()

    fun navigate(route: Route) {
        _route.trySend(route)
    }

}