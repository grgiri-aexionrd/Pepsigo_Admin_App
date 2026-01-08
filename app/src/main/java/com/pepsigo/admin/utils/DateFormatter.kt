package com.pepsigo.admin.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatExpiryDate(input: String?): String {
    if (input.isNullOrBlank()) return ""

    return try {
        val zonedDateTime = ZonedDateTime.parse(input)
        val localDate = zonedDateTime.toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        localDate.format(formatter)
    } catch (e: Exception) {
        ""
    }
}

// or use this
//OffsetDateTime.parse(input)
//.toLocalDate()
//.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))