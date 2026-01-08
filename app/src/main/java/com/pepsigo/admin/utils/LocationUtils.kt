package com.pepsigo.admin.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

val DEFAULT_LOCATION = LatLng(12.9716, 77.5946) // Bangalore

@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    context: Context,
    onLocationReady: (LatLng) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    fused.lastLocation.addOnSuccessListener { loc ->
        loc?.let { onLocationReady(LatLng(it.latitude, it.longitude)) }
    }
}

fun ensureGpsEnabled(
    activity: Activity,
    onEnabled: () -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1000
    ).build()

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)

    val client = LocationServices.getSettingsClient(activity)
    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener { onEnabled() }
    task.addOnFailureListener { ex ->
        if (ex is ResolvableApiException) {
            try {
                ex.startResolutionForResult(activity, 1001)
            } catch (_: Exception) { }
        }
    }
}