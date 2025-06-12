package com.app.weatherforecast.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * Emits a non-null `R` from the transformed `value` only when it changes
 * from `null` to a non-`null` state. Ignores subsequent identical non-`null` values
 * until the value becomes `null` and then non-`null` again.
 *
 * Example:
 * If `value(state)` produces: null, null, "Hello", "Hello", null, "World"
 * This flow emits: "Hello", "World"
 */
fun <T: Any, R: Any> Flow<T>.optional(value: suspend (T) -> R?) = this
    .map { state -> value(state) }
    .distinctUntilChanged { s1, s2 -> s1 != null && s2 != null }
    .mapNotNull { it }