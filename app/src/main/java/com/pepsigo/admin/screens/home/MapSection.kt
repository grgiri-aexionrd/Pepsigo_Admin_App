package com.pepsigo.admin.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.MapSectionState

@Composable
fun MapSection(
    mapState: MapSectionState,
    modifier: Modifier = Modifier,
    onMapLoaded: () -> Unit
) {
//    Box(modifier = modifier) {
//
//        // ✅ GoogleMap ALWAYS exists
//        if (mapState is MapSectionState.Loaded) {
//            MapContent(
//                admins = mapState.admins,
//                customers = mapState.customers,
//                deliveryExecutives = mapState.deliveryExecutives,
//                modifier = modifier
//            )
//        } else {
//            // Create map ONCE with empty lists
//            MapContent(
//                admins = emptyList(),
//                customers = emptyList(),
//                deliveryExecutives = emptyList(),
//                modifier = modifier
//            )
//        }
//
//        // 🔹 Overlay states
//        when (mapState) {
//            MapSectionState.Loading -> MapLoading(modifier)
//            is MapSectionState.Error -> MapError(
//                message = mapState.message,
//                modifier = modifier
//            )
//            else -> Unit
//        }
//    }

    when (mapState) {

        MapSectionState.Loading -> {
            MapLoading(modifier)
        }

        is MapSectionState.Error -> {
            MapError(
                message = mapState.message,
                modifier = modifier
            )
        }

        is MapSectionState.Loaded -> {
            MapContent(
                admins = mapState.admins,
                customers = mapState.customers,
                deliveryExecutives = mapState.deliveryExecutives,
                modifier = modifier,
                onMapLoaded = onMapLoaded
            )
        }
    }
}

@Composable
fun MapLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MapError(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}