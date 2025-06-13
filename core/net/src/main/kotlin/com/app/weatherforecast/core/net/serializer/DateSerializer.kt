package com.app.weatherforecast.core.net.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object DateSerializer : KSerializer<LocalDateTime> {

    val targetClass = LocalDateTime::class

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(DateSerializer::class.java.simpleName, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(decoder.decodeString().toLong()), ZoneId.systemDefault()
    )

    override fun serialize(encoder: Encoder, value: LocalDateTime) = Unit
}