package com.pepsigo.admin.screens.routes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.DeliveryExecutiveAssignmentStatus
import com.pepsigo.admin.domainLayer.RouteUseCase
import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.model.LocationUiModel
import com.pepsigo.admin.model.RouteFormState
import com.pepsigo.admin.model.RouteUiModel
import com.pepsigo.admin.model.RouteUiState
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssignDeliveryExecutive(
    val deliveryExecutives: List<FreeDeliveryExecutive>,
    val error:String? = null , // or type AppError
    val selected: FreeDeliveryExecutive? = null,
    val selectedError: Boolean = false
)
data class FreeDeliveryExecutive(
    val delExecId: Int,
    val delExecName: String,
    val status: DeliveryExecutiveAssignmentStatus
)

data class AssignRouteSheet(
    val isVisible: Boolean = false,
    val assignRoute: RouteUiModel? = null,
    val assignError: String? = null
)




class RouteViewModel(private val usecase: RouteUseCase) : ViewModel() {

    // UI state exposed to the UI to show route list,add,edit
    private val _routes = MutableStateFlow<RouteUiState>(RouteUiState.Loading)
    val routes: StateFlow<RouteUiState> = _routes.asStateFlow()

    // Ui form to add or edit
    private val _formState = MutableStateFlow(RouteFormState())
    val formState = _formState.asStateFlow()

    // ui state to show delivery executives
    private val _deliveryExecutives = MutableStateFlow(AssignDeliveryExecutive(emptyList()))
    val deliveryExecutives = _deliveryExecutives.asStateFlow()

    // ui state to show bottom sheet
    private val _sheetState = MutableStateFlow(AssignRouteSheet())
    val sheetState = _sheetState.asStateFlow()


    init {
        getRouteList()
        getDeliveryExecutives()
    }

    fun getRouteList(snackbarMessage: String? = null, snackbarError: Boolean = false) {
        // Implement fetching logic here
        _routes.value = RouteUiState.Loading
        viewModelScope.launch {
            usecase.getRoutes()
                .onSuccess { routes ->
                    _routes.value = RouteUiState.RoutesList(
                        routes = routes,
                        snackbarMessage = snackbarMessage,
                        snackbarError = snackbarError
                    )
                }
                .onFailure { error ->
                    _routes.value = RouteUiState.RoutesList(
                        routes = emptyList(),
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        snackbarError = true
                    )
                }
        }
    }

    fun getDeliveryExecutives() {
        viewModelScope.launch {
            usecase.getFreeDeliveryExecutives()
                .onSuccess { deliveryExecutives ->
                    _deliveryExecutives.value = deliveryExecutives
                }
                .onFailure { error ->
                    _deliveryExecutives.value = AssignDeliveryExecutive(emptyList(), (error as AppError).userFriendlyMessage)
                }
        }

    }

    fun refreshRouteScreen() = getRouteList()

    fun onAssignRouteClick(route: RouteUiModel) {
        _sheetState.value = AssignRouteSheet(
            isVisible = true,
            assignRoute = route
        )
    }

    fun onSheetDismiss() {
        _sheetState.value = AssignRouteSheet()
        _deliveryExecutives.value = _deliveryExecutives.value.copy(selected = null)
    }

    fun updateSelectedDeliveryExecutive(exec: FreeDeliveryExecutive?){
        _deliveryExecutives.value = _deliveryExecutives.value.copy(selected = exec)
    }

    fun assignDeliveryExecutiveToRoute(route: RouteUiModel, exec: FreeDeliveryExecutive?) {
        if (exec == null){
            _deliveryExecutives.value = _deliveryExecutives.value.copy(selectedError = true)
            return
        }

        viewModelScope.launch {
            val result = usecase.assignDeliveryExecutiveToRoute(exec.delExecId, route.id.toInt())
            result.onSuccess {
                _sheetState.value = AssignRouteSheet()
//                _deliveryExecutives.value = _deliveryExecutives.value.copy(selected = null)
                _routes.update { state ->
                    when(state){
                        is RouteUiState.RoutesList -> state.copy(snackbarMessage = it.message, snackbarError = false)
                        else -> state
                    }
                }
                getDeliveryExecutives()
            }
                .onFailure { error ->
//                    _sheetState.value = AssignRouteSheet(assignError = (error as AppError).userFriendlyMessage)
                    _sheetState.value = _sheetState.value.copy(assignError = (error as AppError).userFriendlyMessage)
                }

        }

    }

    fun addRoute() {
        // Implement add logic here
        _routes.value = RouteUiState.Loading
        viewModelScope.launch {
           val result =  usecase.getLocations()
            if (result is LocationResult.Success) {
                _routes.value = RouteUiState.AddRoute()
                _formState.value = RouteFormState( // initialize form
                    routeName = "",
                    locations = result.data
                )
            } else {
                _routes.value = RouteUiState.AddRoute(
                    snackbarMessage = (result as LocationResult.Error).message,
                    snackbarError = true
                )
                _formState.value = RouteFormState( // initialize form
                    routeName = "",
                    locations = emptyList(),
                    errorMessage = result.message
                )
            }

        }

    }

    fun toggleLocation(id: String) {
        _formState.update { form ->
            form.copy(
                locations = form.locations.map {
                    if (it.id == id) it.copy(isSelected = !it.isSelected) else it
                }
            )
        }
    }

    fun moveLocation(from: Int, to: Int) {
        _formState.update { form ->
            form.copy(
                locations = form.locations.toMutableList().apply {
                    add(to, removeAt(from))
                }
            )
        }
    }

    fun updateRouteName(name: String) {
        _formState.update { it.copy(routeName = name) }
    }

    fun editRoute(route: RouteUiModel) {
        viewModelScope.launch {
            val result = usecase.getLocations()
            Log.d("RouteVM", "Editing route fetch location: $result")
            val allLocations = if (result is LocationResult.Success) {
                result.data
            } else {
                emptyList()
            }

            if(allLocations.isEmpty() && result is LocationResult.Error ) {
                _routes.value = RouteUiState.EditRoute(
                    routeId = route.id,
                    snackbarMessage = result.message,
                    snackbarError = true
                )
            }else {
                _routes.value = RouteUiState.EditRoute(
                    routeId = route.id,
                    snackbarMessage = null,
                    snackbarError = false
                )
            }



            val merged = allLocations.map { location ->
                val matched = route.locations.find { it.id == location.id }
                if (matched != null) {
                    // location already assigned → selected = true
                    location.copy(
                        isSelected = true,
                        isEnabled = matched.isEnabled  // keep route-specific flags
                    )
                } else {
                    // available but not in route
                    location.copy(
                        isSelected = false
                    )
                }
            }

            // 3) Reorder so selected locations come first (in saved route order)
            val selectedIds = route.locations.map { it.id }

            val ordered = merged.sortedBy { loc ->
                // items in route should appear first in the same order
                selectedIds.indexOf(loc.id).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }

            // 4) Update UI state
//            _routes.value.update { RouteUiState.EditRoute(routeId = route.id) }

            // 5) Initialize formState for UI
            _formState.value = RouteFormState(
                routeName = route.routeName,
                locations = ordered,
                errorMessage = if (result is LocationResult.Error) result.message else null
            )
        }
    }

    fun saveRoute(routeId: String? = null) {
        val form = _formState.value
        if( form.routeName.isBlank() || form.locations.none { it.isSelected } ) {
            _formState.value = form.copy(formErrorMessage = "Route name and location cannot be empty",
                formError = true)
            return
        }

        val selectedLocationIds = form.locations
            .filter { it.isSelected }
            .map { it.id.toInt() }

        Log.d(
            "RouteVM",
            "Saving route: name=${form.routeName}, Selected Location IDs (API): $selectedLocationIds"
        )

        viewModelScope.launch {
            val result =
                if (routeId == null) {
                    // ADD
                    usecase.createRoute(
                        name = form.routeName,
                        locationsIds = selectedLocationIds
                    )
                } else {
                    // EDIT
                    usecase.updateRoute(
                        id = routeId,
                        name = form.routeName,
                        locationsIds = selectedLocationIds
                    )
                }

            result.onSuccess { response ->
                Log.d("RouteVM", "Route saved successfully: $response")
                if (routeId == null) {
                    // ADD ROUTE SUCCESS
                    // 1) Show snackbar
                    _routes.value = RouteUiState.AddRoute(
                        snackbarMessage = response.message,
                        snackbarError = false
                    )

                    // 2) Reset form for clean slate
                    _formState.value = RouteFormState(
                        routeName = "",
                        locations = form.locations.map { it.copy(isSelected = false) },
                        formError = false,
                        formErrorMessage = null
                    )

                } else {
                    // EDIT ROUTE SUCCESS
                    // Go back to list with a success snackbar
                    getRouteList( snackbarMessage = response.message,
                        snackbarError = false
                    )

                }
            }
            result.onFailure { error ->
                Log.e("RouteVM", "Error saving route: $error")
                if (routeId == null) {
                    // ADD ROUTE FAIL
                    _routes.value = RouteUiState.AddRoute(
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        snackbarError = true
                    )
                } else {
                    // EDIT ROUTE FAIL
                    _routes.value = RouteUiState.EditRoute(
                        routeId = routeId,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        snackbarError = true
                    )
                }
            }
        }
    }



    fun toggleRoute(route: RouteUiModel) {
        // Implement toggle logic here
        val current = _routes.value
        if (current !is RouteUiState.RoutesList) return
        val list = current.routes.toMutableList()
        val index = list.indexOfFirst { it.id == route.id }
        val previousItem = list[index]
        val toggled = previousItem.copy(enabled = !previousItem.enabled,isToggling = true)
        // 1️⃣ Optimistic update
        list[index] = toggled
        _routes.value = current.copy(routes = list)
        viewModelScope.launch {
            Log.d("RouteVM", "Toggling route status for id: ${route.id}")
            val result = usecase.toggleRoute(route.id )
            Log.d("RouteVM", "toggleRoute: $result")
            result.onSuccess { successResponse ->
                val updatedList = _routes.value.let{
                    (it as? RouteUiState.RoutesList)?.routes?.toMutableList()
                } ?: return@onSuccess
                val updatedIndex = updatedList.indexOfFirst { it.id == route.id }
                updatedList[updatedIndex] = toggled.copy(isToggling = false)
                _routes.value = RouteUiState.RoutesList(
                    routes = updatedList,
                    snackbarMessage = successResponse.message,
                    snackbarError = false
                )

            }
                .onFailure { error ->
                    val updatedList = _routes.value.let{
                        (it as? RouteUiState.RoutesList)?.routes?.toMutableList()
                    } ?: return@onFailure
                    val updatedIndex = updatedList.indexOfFirst { it.id == route.id }
                    // Revert the optimistic update
                    updatedList[updatedIndex] = previousItem.copy(isToggling = false)
                    _routes.value = RouteUiState.RoutesList(
                        routes = updatedList,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                        snackbarError = true
                    )

                    }
        }

    }

    fun clearSnackbar() {
        val currentState = _routes.value
        _routes.value = when (currentState) {
            is RouteUiState.AddRoute -> currentState.copy(snackbarMessage = null)
            is RouteUiState.EditRoute -> currentState.copy(snackbarMessage = null)
            is RouteUiState.RoutesList -> currentState.copy(snackbarMessage = null)
            else -> currentState
        }

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val usecase = application.container.routeUseCase
                RouteViewModel(usecase)
            }
        }
    }



}
