package com.pepsigo.admin.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.UserLocationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.pepsigo.admin.R
import com.pepsigo.admin.utils.bitmapDescriptorFromVector

@Composable
fun MapContent(
    admins: List<UserLocationUI>,
    customers: List<UserLocationUI>,
    deliveryExecutives: List<UserLocationUI>,
    modifier: Modifier = Modifier,
    onMapLoaded: () -> Unit
) {
    val context = LocalContext.current
    val defaultPointer = LatLng(12.7504487, 78.3434800)

    // remember icon
    var adminIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var customerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var deliveryIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    // test marker
    val testMarkerLocation = LatLng(
        defaultPointer.latitude + 0.001,
        defaultPointer.longitude + 0.001
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPointer, 12f)
    }

    // If a marker was clicked, update camera
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }



    // 🔥 FIX 1: Make marker states stable & remember them
    val adminMarkers = remember(admins) {
        admins.map { MarkerState(LatLng(it.lat, it.lng)) }
    }

    val customerMarkers = remember(customers) {
        customers.map { MarkerState(LatLng(it.lat, it.lng)) }
    }

    val deliveryMarkers = remember(deliveryExecutives) {
        deliveryExecutives.map { MarkerState(LatLng(it.lat, it.lng)) }
    }

    // Animate whenever selectedLocation changes
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            adminIcon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE
            )
            customerIcon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
            deliveryIcon = bitmapDescriptorFromVector(
                context,
                R.drawable.icons8_delivery_boy_48,
//                sizeDp = 40
            )

            onMapLoaded() // 🔑 propagate to parent
        }
    ) {

        // test marker
        Marker(
            state = rememberUpdatedMarkerState( testMarkerLocation),
            title = "Test Marker",
            snippet = "Near default location",
            icon = deliveryIcon
        )


        // Admin markers (blue)
        adminMarkers.forEachIndexed { index, markerState ->
            Marker(
                state = markerState,
                title = admins[index].title,
                snippet = "Admin - ${admins[index].subtitle}",
                icon = adminIcon,
//                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                onClick = {
                    selectedLocation = markerState.position
                    false
                }
            )
        }

        // Customer markers (green)
        customerMarkers.forEachIndexed { index, markerState ->
            Marker(
                state = markerState,
                title = customers[index].title,
                snippet = customers[index].subtitle ?: "Customer",
                icon = customerIcon,
//                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                onClick = {
                    selectedLocation = markerState.position
                    false
                }
            )
        }

        // Delivery markers (orange)
        deliveryMarkers.forEachIndexed { index, markerState ->
            Marker(
                state = markerState,
                title = deliveryExecutives[index].title,
                snippet = "Delivery - ${deliveryExecutives[index].subtitle}",
                icon = deliveryIcon,
//                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                onClick = {
                    selectedLocation = markerState.position
                    false
                }
            )
        }
    }

    }


