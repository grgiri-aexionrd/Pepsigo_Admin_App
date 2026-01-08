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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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

@Composable
fun MapScreen(
    placesClient: PlacesClient,
    onLocationPicked: (Double, Double) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 14f)
    }
    val markerState = rememberUpdatedMarkerState(
        position = selectedPosition ?: DEFAULT_LOCATION
    )

    // 🔹 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
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
        if (searchQuery.isBlank()) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ensureGpsEnabled(activity) {
                    fetchCurrentLocation(context) { latLng ->
                        selectedPosition = latLng
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            } else {
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
            onMapClick = { latLng ->
                selectedPosition = latLng
                markerState.position = latLng
            }
        ) {
            selectedPosition?.let {
                Marker(
                    state = markerState,
                    draggable = true
                )
            }
        }

        // 🔹 Floating Search Bar + Back Arrow
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Back arrow in floating circle
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
//                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                // Rounded search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        if (query.isNotBlank()) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(query)
                                .build()
                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    predictions = response.autocompletePredictions
                                }
                                .addOnFailureListener { predictions = emptyList() }
                        } else predictions = emptyList()
                    },
                    placeholder = { Text("Search location") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .weight(1f)
//                        .shadow(6.dp, RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(32.dp))
                )
            }

            // 🔹 Predictions dropdown
            if (predictions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    LazyColumn {
                        items(predictions) { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val request = FetchPlaceRequest.builder(
                                            prediction.placeId,
                                            listOf(Place.Field.LOCATION)
                                        ).build()
                                        placesClient.fetchPlace(request)
                                            .addOnSuccessListener { response ->
                                                response.place.location?.let { latLng ->
                                                    selectedPosition = latLng
                                                    markerState.position = latLng
                                                    cameraPositionState.position =
                                                        CameraPosition.fromLatLngZoom(latLng, 15f)
                                                    predictions = emptyList()
                                                    searchQuery =
                                                        prediction.getFullText(null).toString()
                                                }
                                            }
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
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

    // 🔹 Sync marker with selected position
    LaunchedEffect(markerState.position) {
        selectedPosition = markerState.position
    }

}