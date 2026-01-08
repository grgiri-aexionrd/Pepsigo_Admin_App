package com.pepsigo.admin.fakeTesting


import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.repository.LocationRepository
import com.pepsigo.admin.screens.location.Location

class FakeLocationRepository : LocationRepository {

    // In-memory list to simulate backend data and allow mutations in fake repo
    private val locations = mutableListOf(
        Location(1, "Bangalore", isEnabled = true),
        Location(2, "Chennai", isEnabled = false), // disabled
        Location(3, "Hyderabad", isEnabled = true),
        Location(4, "Mumbai", isEnabled = true),
        Location(5, "Delhi", isEnabled = false)    // disabled
    )

    override suspend fun getLocations(): LocationResult<List<Location>> {
        // return a copy to avoid accidental external mutation
        return LocationResult.Success(locations.toList())
    }

    override suspend fun getLocationById(id: Int): LocationResult<Location> {
        val found = locations.find { it.id == id }
        return if (found != null) {
            LocationResult.Success(found)
        } else {
            LocationResult.Error("Location with id=$id not found")
        }
    }

    override suspend fun addLocation(name: String): LocationResult<Location> {
        val newId = (locations.maxOfOrNull { it.id } ?: 0) + 1
        val newLocation = Location(newId, name, isEnabled = true)
        locations.add(newLocation)
        return LocationResult.Success(newLocation, "Location created")
    }

    override suspend fun updateLocation(id: Int, name: String): LocationResult<Location> {
        val index = locations.indexOfFirst { it.id == id }
        return if (index >= 0) {
            val updated = locations[index].copy(name = name)
            locations[index] = updated
            LocationResult.Success(updated, "Location updated")
        } else {
            LocationResult.Error("Location with id=$id not found")
        }
    }

    override suspend fun toggleStatus(id: Int): LocationResult<Unit> {
        val index = locations.indexOfFirst { it.id == id }
        return if (index >= 0) {
            val current = locations[index]
            locations[index] = current.copy(isEnabled = !current.isEnabled)
            LocationResult.Success(Unit, "Status toggled")
        } else {
            LocationResult.Error("Location with id=$id not found")
        }
    }
}