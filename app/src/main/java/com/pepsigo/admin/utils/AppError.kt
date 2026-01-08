package com.pepsigo.admin.utils

sealed class AppError (message: String,
    cause: Throwable? = null): Throwable(message,cause) {
     class Network( message: String) : AppError(message)
     class Server(val code: Int,  message: String) : AppError(message)
     class Unknown( message: String,  cause: Throwable? = null) : AppError(message,cause)

    val userFriendlyMessage: String
        get() = when (this) {
            is Network -> "No internet connection"
            is Server -> when {
                code >= 500 -> "Server error. Please try again later."
                code == 401 -> "Unauthorized request. Please login."
                code == 403 -> "You do not have permission to perform this action."
                code == 404 -> "$message"
//                    "Requested resource was not found."
                else -> "Request failed ($code,$message). Try again."
                // need to add 422 code.

            }
            is Unknown -> "Unexpected error occurred"
        }
}


