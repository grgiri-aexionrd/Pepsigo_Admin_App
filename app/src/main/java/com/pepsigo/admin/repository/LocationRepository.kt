package com.pepsigo.admin.repository

import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.screens.location.Location

interface LocationRepository {
    suspend fun getLocations(): LocationResult<List<Location>>
    suspend fun getLocationById(id: Int): LocationResult<Location>
    suspend fun addLocation(name: String): LocationResult<Location>
    suspend fun updateLocation(id: Int, name: String): LocationResult<Location>
    suspend fun toggleStatus(id: Int): LocationResult<Unit>

}