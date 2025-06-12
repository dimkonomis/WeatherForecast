package com.app.weatherforecast.feature.location.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.core.utils.getJSON
import kotlinx.coroutines.withContext
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocationSerializer @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : Serializer<Location> {

    private val module = SerializersModule {
        polymorphic(Location::class) {
            subclass(Location.NotAvailable::class)
            subclass(Location.Available::class)
        }
    }

    private val json = getJSON(module = module)

    override val defaultValue: Location = Location.NotAvailable

    @Suppress("TooGenericExceptionCaught")
    override suspend fun readFrom(input: InputStream): Location = withContext(dispatcherProvider.io) {
        try {
            val availableBytes = input.available()
            val buffer = ByteArray(availableBytes).also {
                input.read(it)
            }
            val content = buffer.decodeToString()

            if (content.isEmpty()) return@withContext defaultValue

            val deserializedState = json.decodeFromString(Location.serializer(), content)

            return@withContext deserializedState
        } catch (e: SerializationException) {
            val message = "Unable to read Location"
            Timber.e(e, message)
            throw CorruptionException(message, e)
        }
    }

    override suspend fun writeTo(t: Location, output: OutputStream) {
        withContext(dispatcherProvider.io) {
            output.write(json.encodeToString(PolymorphicSerializer(Location::class), t).encodeToByteArray())
        }
    }
}