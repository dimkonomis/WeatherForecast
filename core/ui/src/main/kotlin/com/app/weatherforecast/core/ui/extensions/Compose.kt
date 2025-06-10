package com.app.weatherforecast.core.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun <T : Any> SingleEventEffect(
    sideEffectChannel: ReceiveChannel<T>,
    lifeCycleState: State = State.STARTED,
    collector: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(sideEffectChannel) {
        lifecycleOwner.repeatOnLifecycle(lifeCycleState) {
            sideEffectChannel.receiveAsFlow().collect(collector)
        }
    }
}