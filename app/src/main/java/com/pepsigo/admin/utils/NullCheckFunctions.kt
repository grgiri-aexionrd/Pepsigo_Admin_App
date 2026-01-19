package com.pepsigo.admin.utils

import android.os.Build
import androidx.annotation.RequiresApi

// ✔ Safe string
fun String?.safeText(default: String = "—"): String =
    this?.takeIf { it.isNotBlank() } ?: default

// ✔ Safe int
fun Int?.safeInt(default: Int = 0): Int =
    this ?: default

// ✔ Safe double → formatted currency
fun Double?.safeAmount(): String =
    this?.toCurrency() ?: "₹ 0.00"

// ✔ Safe double → String
fun Double?.safeString(default: String = ""): String =
    this?.toString() ?: default


// ✔ Safe date formatter
@RequiresApi(Build.VERSION_CODES.O)
fun String?.safeDate(): String =
    if (this == null) "—" else formatExpiryDate(this)

// ✔ Safe Percentage
fun Double?.safePercent(default: String = "0%"): String =
    if (this == null) default else "${this.toInt()}%"

//Api Nullable
fun String.toApiNullable(): String? =
    this.takeIf { it.isNotBlank() && it != "—" }

fun String.toDoubleApiNullable(): Double? {
    val value = this.toDoubleOrNull()
    return if (value == null || value == 0.0) null else value
}


