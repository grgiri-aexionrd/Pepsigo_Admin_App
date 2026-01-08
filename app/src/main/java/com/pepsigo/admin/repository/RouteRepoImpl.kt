package com.pepsigo.admin.repository

import com.pepsigo.admin.model.GetRoutesDto
import com.pepsigo.admin.model.LocationDto
import com.pepsigo.admin.model.RouteAddEditRequest
import com.pepsigo.admin.model.RouteAssignRequest
import com.pepsigo.admin.model.RouteAssignResponse
import com.pepsigo.admin.model.RouteResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class RouteRepoImpl(private val apiService: ApiService): RouteRepository {
    override suspend fun getRoutes(): Result<List<GetRoutesDto>> {
        return wrapError {
            val response = apiService.getRoutes()
            response.data ?: emptyList()
        }

    }

    override suspend fun assignDeliveryExecutiveToRoute(
        delExecId: Int,
        routeId: Int
    ): Result<RouteResponse<RouteAssignResponse>> {
        return wrapError {
            val response = apiService.assignDeliveryExecutiveToRoute(
                RouteAssignRequest(
                    delExecId,
                    routeId
                )
            )
            response
        }

    }

    override suspend fun createRoute(newRoute: RouteAddEditRequest): Result<RouteResponse<LocationDto>>{
        return wrapError {
            val response = apiService.createRoute(newRoute)
            response
        }

    }

    override suspend fun updateRoute(
        id: Int,
        newRoute: RouteAddEditRequest
    ): Result<RouteResponse<LocationDto>> {
        return wrapError {
            val response = apiService.updateRoute(id = id, newRoute)
            response
        }
    }

    override suspend fun toggleRoute(id: Int): Result<RouteResponse<LocationDto>> {
        return wrapError {
            val response = apiService.toggleRoute(id)
            response
        }
    }

}