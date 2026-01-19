package com.pepsigo.admin.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.mapper.toInventoryUi
import com.pepsigo.admin.mapper.toMapUi
import com.pepsigo.admin.mapper.toSalesUi
import com.pepsigo.admin.mapper.toUsersUi
import com.pepsigo.admin.model.MapSectionState
import com.pepsigo.admin.model.MetricsUiState
import com.pepsigo.admin.model.ScreenUiState
import com.pepsigo.admin.repository.AuthRepository
import com.pepsigo.admin.repository.DashboardRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogoutViewModel(
    private val repository: AuthRepository,
    private val dashboardRepo: DashboardRepo
) : ViewModel() {

//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _metricsState = MutableStateFlow(MetricsUiState())
    val metricsState: StateFlow<MetricsUiState> = _metricsState.asStateFlow()

    private val _mapState = MutableStateFlow<MapSectionState>(MapSectionState.Loading)
    val mapState: StateFlow<MapSectionState> = _mapState.asStateFlow()

    private val _screenState = MutableStateFlow(ScreenUiState())
    val screenState: StateFlow<ScreenUiState> = _screenState.asStateFlow()


    private val _logoutSuccess = MutableSharedFlow<Boolean>()
    val logoutSuccess = _logoutSuccess.asSharedFlow()

    private val previousDeliveryLocations = mutableMapOf<Int, LatLng>()

    init {
        getDashboardData()
        Log.d("logoutViewModel","init called")
    }

    fun getDashboardData(refresh: Boolean = false) {
        viewModelScope.launch {
            // mark refreshing/loading
            _screenState.update { it.copy(isRefreshing = refresh) }

            dashboardRepo.fetchDashboardData()
                .onSuccess { domain ->

                    // Generate new UI models
                    val newMetrics = MetricsUiState(
                        salesMetrics = domain.metrics.toSalesUi(),
                        inventoryMetrics = domain.metrics.toInventoryUi(),
                        userMetrics = domain.metrics.toUsersUi(),
                        metricsError = null
                    )

                    // emit only if metrics actually changed
                    if (_metricsState.value != newMetrics) {
                        _metricsState.value = newMetrics
                    }

                    val newMapState = domain.users.toMapUi(previousDeliveryLocations) // returns MapSectionState.Loaded
                    Log.d(
                        "MAP_EQ",
                        "equal=${_mapState.value == newMapState}"
                    )

                    if (_mapState.value != newMapState) {
                        _mapState.value = newMapState
                    }

                    // clear loading flags
                    _screenState.update {
                        it.copy( isRefreshing = false)
                    }


                }
                .onFailure { error ->

                    // metrics error only affects metrics section
                    _metricsState.update {
                        it.copy(metricsError = error.message ?: "Unknown error")
                    }

                    _screenState.update {
                        it.copy(isRefreshing = false)
                    }

                }
        }
    }

    // called from pull-to-refresh
    fun refreshDashboard() = getDashboardData(refresh = true)


    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutSuccess.emit(true)
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.authRepository
                val dashboardRepo = application.container.dashboardRepository
                LogoutViewModel(repository, dashboardRepo)
            }
        }
    }
}