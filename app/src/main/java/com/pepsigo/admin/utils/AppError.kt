package com.pepsigo.admin.utils

import okio.IOException
import retrofit2.HttpException
import kotlinx.coroutines.CancellationException

sealed class AppError (open val errorMessage: String,
    cause: Throwable? = null): Throwable(errorMessage,cause) {
     class Network( override val errorMessage: String = "No internet connection") : AppError(errorMessage)
     class Server(val code: Int,  override val errorMessage: String) : AppError(errorMessage)
     class Unknown( override val errorMessage: String,  cause: Throwable? = null) : AppError(errorMessage,cause)

    val userFriendlyMessage: String
        get() = when (this) {
            is Network -> "No internet connection"
            is Server -> when (code) {
                400 -> errorMessage               // Business rule failure
                401 -> errorMessage   //"Unauthorized request. Please login."
                403 -> errorMessage   // Account disabled / forbidden
                404 -> errorMessage               // Not found
                422 -> errorMessage               // Validation error
                in 500..599 ->
                    "Server error. Please try again later."
                else ->
                    "Request failed ($code). Try again."
            }
            is Unknown -> "Unexpected error occurred"
        }
}

fun Throwable.toAppError(): AppError = when (this) {

    is CancellationException -> {
        // ❗ Never convert coroutine cancellation
        throw this
    }

    is AppError -> this

    is IOException -> {
        AppError.Network()
    }

    is HttpException -> {
        val code = code()

        val rawBody = try {
            response()?.errorBody()?.string()
        } catch (_: Throwable) {
            null
        }

        val message = parseErrorMessage(rawBody) ?: message()

        AppError.Server(
            code = code,
            errorMessage = message
        )
    }

    else -> {
        AppError.Unknown(
            errorMessage = message ?: "Unexpected error",
            cause = this
        )
    }
}


