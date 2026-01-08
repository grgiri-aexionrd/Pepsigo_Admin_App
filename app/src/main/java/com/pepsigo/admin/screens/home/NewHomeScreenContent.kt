package com.pepsigo.admin.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.MapSectionState
import com.pepsigo.admin.model.MetricsUiState

@Composable
fun NewHomeScreenContent(
    metricsState: MetricsUiState,
    mapState: MapSectionState,
    mapLoaded: Boolean,
    onMapLoaded: () -> Unit

){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // 🔹 Top scrollable content (metrics, titles, etc.)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ){
            Text(
                text = "Metrics Overview",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (metricsState.metricsError != null) {
                MetricCardError(metricsState.metricsError)
            } else {
                MetricsTabsSection(
                    salesMetrics = metricsState.salesMetrics,
                    inventoryMetrics = metricsState.inventoryMetrics,
                    usersMetrics = metricsState.userMetrics
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track Location",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 🔥 Map fills leftover space safely
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   // ⭐ MAP TAKES REMAINING SPACE
        ){
            key("map_stable"){
                MapSection(
                    mapState = mapState,
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = onMapLoaded,

                    )
            }

            // ✅ Mask ONLY the map area
            if (!mapLoaded) {
                MapPlaceholder()
            }

        }

    }

}