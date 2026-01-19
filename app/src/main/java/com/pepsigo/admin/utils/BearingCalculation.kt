package com.pepsigo.admin.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun calculateBearing(from: LatLng, to: LatLng): Float {
    val lat1 = Math.toRadians(from.latitude)
    val lon1 = Math.toRadians(from.longitude)
    val lat2 = Math.toRadians(to.latitude)
    val lon2 = Math.toRadians(to.longitude)

    val dLon = lon2 - lon1

    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) -
            sin(lat1) * cos(lat2) * cos(dLon)

    return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
}