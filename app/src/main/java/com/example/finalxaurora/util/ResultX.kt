package com.example.finalxaurora.util

sealed class ResultX<out T> {
    data class Ok<T>(val value: T) : ResultX<T>()
    data class Err(val message: String, val throwable: Throwable? = null) : ResultX<Nothing>()

    inline fun <R> map(transform: (T) -> R): ResultX<R> = when (this) {
        is Ok -> Ok(transform(value))
        is Err -> this
    }
}
