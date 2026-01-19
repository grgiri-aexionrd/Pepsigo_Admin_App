package com.pepsigo.admin.utils

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun<T> wrapError(crossinline action: suspend () -> T): Result<T> {
    return try {
        Result.success(action())
    } catch (e: IOException) {
        Result.failure(AppError.Network())
    } catch (e: HttpException) {
        // also error body response needs to be parsed
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Throwable) {
            null
        }
        val message = parseErrorMessage(errorBody) ?: e.message()

        Result.failure(AppError.Server(e.code(), message))
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        Result.failure(AppError.Unknown(t.message ?: "Unexpected error", t))
    }
}

/**
 * Extracts the "error" field from backend JSON:
 * { "error": "Invalid credentials." }
 */
fun parseErrorMessage(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return try {
        val json = JSONObject(raw)

        when {
            json.has("error") -> json.optString("error")
            json.has("message") -> json.optString("message")
            else -> raw
        }
    } catch (_: Throwable) {
        raw
    }
}