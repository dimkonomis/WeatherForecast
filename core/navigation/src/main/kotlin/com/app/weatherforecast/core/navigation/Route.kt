package com.app.weatherforecast.core.navigation

import com.app.weatherforecast.core.navigation.Route.PopupTo
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface Route {
    val popupTo: PopupTo?
        get() = null

    @Serializable
    data class PopupTo(val route: Route, val inclusive: Boolean)
}

@Serializable
data class LocationRoute(
    val mode: Mode
) : Route {
    enum class Mode {
        Create, Update
    }
}

@Serializable
data class WeatherRoute(
    @Transient
    override val popupTo: PopupTo? = null
) : Route

data class Pop(
    val route: Route, val inclusive: Boolean = false
) : Route

data class Back(val arguments: List<Pair<String, Any>> = emptyList()) : Route {

    companion object {
        val back = Back()
    }

}