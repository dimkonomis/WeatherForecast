package com.app.weatherforecast.core.model

sealed interface AsyncResult<out T> {

    data class Success<out T>(val value: T) : AsyncResult<T>
    data class Failure(val exception: Throwable) : AsyncResult<Nothing> {
        override fun equals(other: Any?): Boolean = other is Failure &&
                (exception == other.exception || exception.message == other.exception.message)
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
    data object Loading : AsyncResult<Nothing>
    data object NotStarted : AsyncResult<Nothing>

    val success: Success<T>?
        get() = this as? Success<T>

    val isSuccess: Boolean
        get() = this is Success<T>

    val failure: Failure?
        get() = this as? Failure

}