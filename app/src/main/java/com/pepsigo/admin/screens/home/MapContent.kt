package com.pepsigo.admin.screens.home

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.pepsigo.admin.utils.calculateBearing
import kotlinx.coroutines.delay

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

    // test marker -- 12.750771, 78.346046 || 12.750487, 78.345433
    val testMarkerLocation = LatLng(12.750487, 78.345433)

    val pointNorth = LatLng(12.751487, 78.345433)   // Move UP
    val pointEast  = LatLng(12.750487, 78.346433)   // Move RIGHT
    val pointSouth = LatLng(12.749487, 78.345433)   // Move DOWN
    val pointWest  = LatLng(12.750487, 78.344433)   // Move LEFT

    val previous = testMarkerLocation
    val testPoints = listOf(pointNorth, pointEast, pointSouth, pointWest)
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            index = (index + 1) % testPoints.size
        }
    }

    val current = testPoints[index]
    val bearing = calculateBearing(testMarkerLocation, current)



    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPointer, 12f)
    }

    // If a marker was clicked, update camera
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SwingRotation"
    )

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
                R.drawable.icons8_truck_top_view_48,
//                sizeDp = 40
            )

            onMapLoaded() // 🔑 propagate to parent
        }
    ) {

        val testAnimatedRotation by animateFloatAsState(
            targetValue = bearing,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            label = "MarkerRotation"
        )

        // test marker
        Marker(
            state = rememberUpdatedMarkerState( testMarkerLocation),
            title = "Test Marker",
            snippet = "Near default location",
            anchor = Offset(0.5f, 0.5f),
            rotation = testAnimatedRotation,
            flat = true,
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

            // Smooth rotation animation
            val animatedRotation by animateFloatAsState(
                targetValue = deliveryExecutives[index].rotation,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                ),
                label = "MarkerRotation"
            )

            Marker(
                state = markerState,
                title = deliveryExecutives[index].title,
                snippet = "Delivery - ${deliveryExecutives[index].subtitle}",
                anchor = Offset(0.5f, 0.5f),
                rotation = animatedRotation,
                flat = true,
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


