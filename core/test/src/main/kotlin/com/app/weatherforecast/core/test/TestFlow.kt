package com.app.weatherforecast.core.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

/**
 * TestFlow is a utility class to test flows in a structured way.
 * It allows you to start a flow, perform operations on it, and verify the received values.
 * If not all received values are handled, it will throw an AssertionError.
 */
class TestFlow<STATE : Any> private constructor(
    private val flow: Flow<STATE>,
    private val scope: CoroutineScope
) {

    private val received: MutableList<STATE> = mutableListOf()
    private val verification = Verification()
    private lateinit var jon: Job

    private fun start() {
        jon = flow
            .onEach {
                received.add(it)
            }
            .launchIn(scope)
    }

    private fun assert() {
        if (received.isNotEmpty()) {
            throw AssertionError("Received value ${received.first()} not handled")
        }
    }

    private fun close() {
        jon.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun TestScope.`when`(operation: suspend () -> Unit): Verification {
        operation()
        // Since we use StandardTestDispatcher on our tests
        // we need to run any tasks that are pending, according to the testScheduler.
        runCurrent()
        return verification
    }

    inner class Verification {
        fun then(vararg values: STATE): Verification {
            verify(*values)
            return this
        }

        private fun verify(vararg values: STATE): Verification {
            values.forEach {
                val value = received.removeAt(0)
                assert(value == it) {
                    """
                        Verification failed
                        Received: $value
                        Expected: $it.
                    """.trimIndent()
                }
            }
            return this
        }
    }

    companion object {

        suspend fun <T : Any> Flow<T>.testFlow(
            scope: CoroutineScope,
            block: suspend TestFlow<T>.() -> Unit
        ) {
            TestFlow(this, scope).apply {
                start()
                block(this)
                assert()
                close()
            }
        }
    }
}