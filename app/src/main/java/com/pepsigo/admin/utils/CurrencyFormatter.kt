package com.pepsigo.admin.utils

import java.text.NumberFormat
import java.util.Locale

 fun Double.toCurrency(): String {
    val locale = Locale.Builder()
        .setLanguage("en")
        .setRegion("IN")   // Region instead of deprecated country param
        .build()

    return NumberFormat.getCurrencyInstance(locale).format(this)
}