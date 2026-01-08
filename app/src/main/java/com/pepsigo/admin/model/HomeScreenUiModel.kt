package com.pepsigo.admin.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


data class MetricsUiState(
    val salesMetrics: List<MetricCardState> = emptyList(),
    val inventoryMetrics: List<MetricCardState> = emptyList(),
    val userMetrics: List<MetricCardState> = emptyList(),
    val metricsError: String? = null
)

data class ScreenUiState(
//    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)


sealed class MetricCardState {


    object Loading : MetricCardState()

    data class Loaded(
        val title: String,
        val value: String,
        val icon: ImageVector,
        val color: Color
    ) : MetricCardState()
}



sealed class MapSectionState {
    object Loading : MapSectionState()

    data class Loaded(
        val admins: List<UserLocationUI>,
        val customers: List<UserLocationUI>,
        val deliveryExecutives: List<UserLocationUI>
    ) : MapSectionState()

    data class Error(val message: String) : MapSectionState()
}

data class UserLocationUI(
    val id: Int,
    val title: String,
    val subtitle: String? = "",
    val lat: Double,
    val lng: Double,
  //  val routeStatus: String? = "", // only for delivery executives
    val category: String   // admin, customer, delivery
)