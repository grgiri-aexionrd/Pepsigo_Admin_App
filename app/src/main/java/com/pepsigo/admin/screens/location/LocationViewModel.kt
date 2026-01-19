package com.pepsigo.admin.screens.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val locations: List<Location> = emptyList(),
    val isDialogOpen: Boolean = false,
    val editingLocation: Location? = null, // null = add, non-null = edit
    val searchQuery: String = "",
    val isRefreshing: Boolean = false
)
data class Location(
    val id: Int,
    val name: String,
    val isEnabled: Boolean
)

class LocationViewModel(private val repository: LocationRepository): ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    init {
        getLocations()
    }

    fun getLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getLocations()) {
                is LocationResult.Success -> _uiState.update {
                    Log.d("LocationViewModel", "Locations fetched successfully: ${result.data}")
                    it.copy(isLoading = false, locations = result.data, errorMessage = null, isRefreshing = false)
                }
                is LocationResult.Error -> _uiState.update {
                    Log.d("LocationViewModel", "Error fetching locations: ${result.message}")
                    it.copy(isLoading = false, errorMessage = result.message, isRefreshing = false)
                }
            }
        }
    }

    fun refreshLocation() {
        _uiState.update { it.copy(isRefreshing = true) }
        getLocations()
    }


    fun openAddDialog() {
        _uiState.update { it.copy(isDialogOpen = true, editingLocation = null) }
    }

    fun openEditDialog(id: Int) {
        viewModelScope.launch {
            Log.d("LocationViewModel", "openEditDialog called with id: $id")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getLocationById(id)) {
                is LocationResult.Success -> {
                    Log.d("LocationViewModel", "Location fetched successfully: ${result.data}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDialogOpen = true,
                            editingLocation = result.data
                        )
                    }
                }

                is LocationResult.Error -> {
                    Log.e("LocationViewModel", "Error fetching location: ${result.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogOpen = false, editingLocation = null) }
    }

    fun saveLocation(name: String, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank()) {
            onResult(false, "Name should not be empty")
            return
        }

        viewModelScope.launch {
            val editing = _uiState.value.editingLocation
            val result = if (editing == null) {
                repository.addLocation(name)
            } else {
                repository.updateLocation(editing.id, name)
            }

            when (result) {
                is LocationResult.Success -> {
                    Log.d("LocationViewModel", "Location saved successfully: ${result.message}")
                    closeDialog()
                    onResult(true, result.message)
                    getLocations()
                }
                is LocationResult.Error -> {
                    Log.d("LocationViewModel", "Error saving location: ${result.message}")
                    onResult(false, result.message)
                }
            }
        }
    }

    fun toggleStatus(location: Location, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.toggleStatus(location.id)
            when (result) {
                is LocationResult.Success -> {
                    Log.d("LocationViewModel", "Location status toggled successfully: ${result.message}")
                    onResult(true, result.message)
                    getLocations()
                }
                is LocationResult.Error -> {
                    Log.d("LocationViewModel", "Error toggling location status: ${result.message}")
                    onResult(false, result.message)
                }
            }
        }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }




    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.locationRepository
                LocationViewModel(repository)
            }
        }

    }
}