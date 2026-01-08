package com.pepsigo.admin.domainLayer

import androidx.compose.ui.text.toLowerCase
import com.pepsigo.admin.model.LocationDto
import com.pepsigo.admin.model.LocationResult
import com.pepsigo.admin.model.LocationUiModel
import com.pepsigo.admin.model.RouteAddEditRequest
import com.pepsigo.admin.model.RouteAssignResponse
import com.pepsigo.admin.model.RouteResponse
import com.pepsigo.admin.model.RouteUiModel
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepo
import com.pepsigo.admin.repository.LocationRepository
import com.pepsigo.admin.repository.RouteRepository
import com.pepsigo.admin.screens.routes.AssignDeliveryExecutive
import com.pepsigo.admin.screens.routes.FreeDeliveryExecutive
import java.util.Locale
import java.util.Locale.getDefault

enum class DeliveryExecutiveAssignmentStatus {
    FREE,
    ASSIGNED
}

class RouteUseCase(
    private val routeRepository: RouteRepository,
    private val locationRepository: LocationRepository,
    private val deliveryRepository: DeliveryExecutiveStatusRepo

) {
    suspend fun getRoutes(): Result<List<RouteUiModel>> {
        val result = routeRepository.getRoutes()
        return result.map { routes ->
            routes.map { getRoutesDto ->
                RouteUiModel(
                id = getRoutesDto.id.toString(),
                routeName = getRoutesDto.name,
                locations = getRoutesDto.routeLocations.map {
                    LocationUiModel(
                        id = it.locationId.toString(),
                        name = it.location.name,
                        isEnabled = it.location.enabled
                    )
                },
                enabled = getRoutesDto.isEnabled
                )
            }
        }
    }

    suspend fun getFreeDeliveryExecutives(): Result<AssignDeliveryExecutive> {
        val result = deliveryRepository.fetchDeliveryExecutiveStatuses()
        return result.map { executives ->
            AssignDeliveryExecutive(
                deliveryExecutives = executives.map {
                    FreeDeliveryExecutive(
                        delExecId = it.id,
                        delExecName = it.delExecName,
                        status = if (it.status.lowercase(getDefault()) == "assigned" ) DeliveryExecutiveAssignmentStatus.ASSIGNED else DeliveryExecutiveAssignmentStatus.FREE

                    )
                }
            )
        }
    }

    suspend fun assignDeliveryExecutiveToRoute(delExecId: Int, routeId: Int): Result<RouteResponse<RouteAssignResponse>> {
        return routeRepository.assignDeliveryExecutiveToRoute(delExecId, routeId)
    }



    suspend fun getLocations(): LocationResult<List<LocationUiModel>> {
        val result = locationRepository.getLocations()
       return if (result is LocationResult.Success) {
            LocationResult.Success(
                result.data.filter{ it.isEnabled }.map {
                    LocationUiModel(
                        id = it.id.toString(),
                        name = it.name,
                        isEnabled = it.isEnabled
                    )
                }
            )
        } else  {
             LocationResult.Error("Failed to fetch locations")
        }
    }

    suspend fun createRoute(name: String, locationsIds: List<Int>): Result<RouteResponse<LocationDto>>{
        return routeRepository.createRoute(
            RouteAddEditRequest(
                name = name,
                locationIds = locationsIds
            )
        )
    }

    suspend fun updateRoute(id: String, name: String, locationsIds: List<Int>): Result<RouteResponse<LocationDto>> {
        return routeRepository.updateRoute(
            id = id.toInt(),
            RouteAddEditRequest(
                name = name,
                locationIds = locationsIds
            )

        )
    }

    suspend fun toggleRoute(id: String): Result<RouteResponse<LocationDto>> {
        return routeRepository.toggleRoute(id.toInt())
    }


}

