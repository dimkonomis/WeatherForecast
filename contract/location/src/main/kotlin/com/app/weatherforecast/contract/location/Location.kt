package com.app.weatherforecast.contract.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Location {

    @Serializable
    data object NotAvailable : Location

    @Serializable
    data class Available(
        @SerialName("name") val name: String = "",
        @SerialName("lat") val lat: Double = 0.0,
        @SerialName("long") val long: Double = 0.0
    ) : Location

    val available: Available?
        get() = this as? Available
}
