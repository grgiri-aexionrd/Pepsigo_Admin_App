package com.pepsigo.admin.utils

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun<T> wrapError(crossinline action: suspend () -> T): Result<T> {
    return try {
        Result.success(action())
    } catch (e: IOException) {
        Result.failure(AppError.Network("No internet connection"))
    } catch (e: HttpException) {
        // also error body response needs to be parsed
        Result.failure(AppError.Server(e.code(), e.message ?: "Server error"))
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        Result.failure(AppError.Unknown(t.message ?: "Unexpected error", t))
    }
}