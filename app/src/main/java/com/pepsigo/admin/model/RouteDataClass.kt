package com.pepsigo.admin.model


sealed class RouteUiState {

    // 🔹 Initial / loading state
    object Loading : RouteUiState()

    // 🔹 Routes list screen
    data class RoutesList(
        val routes: List<RouteUiModel> = emptyList(),
        val selectedRoute: RouteUiModel? = null,
        val snackbarMessage: String? = null,
        val snackbarError: Boolean = false
    ) : RouteUiState()

    //🔹 Add new route screen
    data class AddRoute(
        val snackbarMessage: String? = null,
        val snackbarError: Boolean = false
    ) : RouteUiState()

    //     🔹 Edit route screen
    data class EditRoute(
        val routeId: String,
        val snackbarMessage: String? = null,
        val snackbarError: Boolean = false
    ) : RouteUiState()


    // 🔹 Saved state (after add/edit complete)
    object Saved : RouteUiState()
}

data class RouteUiModel(
    val id: String,
    val routeName: String,
//    val deliveryBoyName: String ="",
    val locations: List<LocationUiModel>,
    val enabled: Boolean,
    val isToggling: Boolean = false
)

data class LocationUiModel(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val isSelected: Boolean = false
)

data class RouteFormState(
    val routeName: String = "",
    val locations: List<LocationUiModel> = emptyList(),
    val errorMessage: String? = null,
    val formError: Boolean = false,
    val formErrorMessage: String? = null
)