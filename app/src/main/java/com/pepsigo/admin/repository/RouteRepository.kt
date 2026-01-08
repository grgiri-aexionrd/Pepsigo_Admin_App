package com.pepsigo.admin.repository

import com.pepsigo.admin.model.GetRoutesDto
import com.pepsigo.admin.model.LocationDto
import com.pepsigo.admin.model.RouteAddEditRequest
import com.pepsigo.admin.model.RouteAssignResponse
import com.pepsigo.admin.model.RouteResponse

interface RouteRepository{
    suspend fun getRoutes(): Result<List<GetRoutesDto>>
    suspend fun assignDeliveryExecutiveToRoute(delExecId: Int, routeId: Int): Result<RouteResponse<RouteAssignResponse>>

    suspend fun createRoute(newRoute: RouteAddEditRequest): Result<RouteResponse<LocationDto>>
    suspend fun updateRoute(id: Int, newRoute: RouteAddEditRequest): Result<RouteResponse<LocationDto>>
    suspend fun toggleRoute(id: Int): Result<RouteResponse<LocationDto>>

}

