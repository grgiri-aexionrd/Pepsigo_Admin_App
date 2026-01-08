package com.pepsigo.admin.model

sealed class LocationResult<out T> {
    data class Success<T>(val data: T, val message: String = "") : LocationResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : LocationResult<Nothing>()
}