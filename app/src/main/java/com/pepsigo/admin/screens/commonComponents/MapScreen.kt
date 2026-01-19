package com.pepsigo.admin.screens.commonComponents

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.pepsigo.admin.utils.DEFAULT_LOCATION
import com.pepsigo.admin.utils.ensureGpsEnabled
import com.pepsigo.admin.utils.fetchCurrentLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.android.libraries.places.api.model.Place
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.ktx.model.cameraPosition
import com.pepsigo.admin.R
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    initialLocation: LatLng?,
    onLocationPicked: (Double, Double) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()

    var selectedPosition by remember { mutableStateOf(initialLocation) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation ?: DEFAULT_LOCATION, 14f)
    }


    // 🔹 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted && selectedPosition == null) {
                ensureGpsEnabled(activity) {
                    fetchCurrentLocation(context) { latLng ->
                        selectedPosition = latLng
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            }
        }
    )

    // 🔹 On screen open
    LaunchedEffect(Unit) {
        when {
            initialLocation != null -> {
                selectedPosition = initialLocation
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(initialLocation, 15f)
            }

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                ensureGpsEnabled(activity) {
                    fetchCurrentLocation(context) { latLng ->
                        selectedPosition = latLng
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔹 Map
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        )

        // 🔹 Floating Search Bar + Back Arrow{
            // Back arrow in floating circle
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
//                        .shadow(4.dp, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }


        // 📍 Uber-style fixed pickup pin
        Column(
            modifier = Modifier.align(Alignment.Center)
                .clickable(
                    onClick = {
                        val newLocation = cameraPositionState.position.target
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(newLocation, 18f),
                                durationMs = 600
                            )
                        }
                        selectedPosition = newLocation
                    }

                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Pin head (replace drawable if needed)
            Icon(
                Icons.Default.PinDrop,
//                painter = painterResource(R.drawable.ic_pickup_pin),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
            // Stem line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(22.dp)
                    .background(Color.Red)
            )

            // X mark (exact ground point)
            Text(
                text = "✕",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

// 🔹 Floating Bottom Button
        Button(
            onClick = {
                selectedPosition?.let {
                    onLocationPicked(it.latitude, it.longitude)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Set Location")
        }

    }

    // 🔹 Update selected position when camera stops moving
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            selectedPosition = cameraPositionState.position.target
        }
    }



}