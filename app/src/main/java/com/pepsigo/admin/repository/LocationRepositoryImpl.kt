package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.screens.location.Location

class LocationRepositoryImpl(
    private val apiService: ApiService
) : LocationRepository {
    override suspend fun getLocations(): LocationResult<List<Location>> {
        return try {
            val response = apiService.getLocations()
            if (response.error != null) {
                LocationResult.Error(response.error)
            } else {
                LocationResult.Success(
                    response.data?.map { it.toDomain() } ?: emptyList(),
                    message = "Locations loaded"
                )
            }
        } catch (e: Exception) {
            LocationResult.Error("Failed to fetch locations", e)
        }
    }

    override suspend fun getLocationById(id: Int): LocationResult<Location> {
        return try {
            val dto = apiService.getLocationById(id) // now returns LocationDto directly
            Log.d("LocationRepository", "getLocationById($id) response = $dto")

            LocationResult.Success(dto.toDomain()) // map to domain model
        } catch (e: Exception) {
            Log.e("LocationRepository", "getLocationById($id) failed", e)
            LocationResult.Error("Failed to fetch location", e)
        }
    }

    override suspend fun addLocation(name: String): LocationResult<Location> {
        return try {
            val response = apiService.addLocation(mapOf("name" to name))
            if (response.error != null) {
                LocationResult.Error(response.error)
            } else {
                LocationResult.Success(
                    response.data!!.toDomain(),
                    message = response.message ?: "Location created successfully"
                )
            }
        } catch (e: Exception) {
            LocationResult.Error("Failed to add location", e)
        }
    }

    override suspend fun updateLocation(
        id: Int,
        name: String
    ): LocationResult<Location> {
        return try {
            val response = apiService.updateLocation(id, mapOf("name" to name))
            if (response.error != null) {
                LocationResult.Error(response.error)
            } else {
                LocationResult.Success(
                    response.data!!.toDomain(),
                    message = response.message ?: "Location updated successfully"
                )
            }
        } catch (e: Exception) {
            LocationResult.Error("Failed to update location", e)
        }
    }

    override suspend fun toggleStatus(id: Int): LocationResult<Unit> {
        return try {
            val response = apiService.toggleStatus(id)
            Log.d("LocationRepository", "toggleStatus($id) response = $response")
            if (response.error != null) {
                LocationResult.Error(response.error)
            } else {
                LocationResult.Success(Unit, response.message ?: "Location status updated successfully")
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "toggleStatus($id) failed", e)
            LocationResult.Error("Failed to update status", e)
        }
    }


}