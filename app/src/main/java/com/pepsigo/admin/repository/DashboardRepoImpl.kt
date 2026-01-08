package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.DashboardData
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class DashboardRepoImpl(private val apiService: ApiService): DashboardRepo {
    override suspend fun fetchDashboardData(): Result<DashboardData> {
        return wrapError {
            val response = apiService.getDashboardData()
            Log.d("DashboardRepoImpl", "Fetched dashboard data: $response")
            response.toDomain()
        }
    }
}