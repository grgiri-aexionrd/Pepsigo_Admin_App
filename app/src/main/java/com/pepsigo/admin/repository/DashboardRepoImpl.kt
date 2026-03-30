package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.DashboardData
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardRepoImpl(private val apiService: ApiService): DashboardRepo {
    override suspend fun fetchDashboardData(): Result<DashboardData> = withContext(Dispatchers.IO) {
        return@withContext wrapError {
            val response = apiService.getDashboardData()
            Log.d("DashboardRepoImpl", "Fetched dashboard data: $response")
            response.toDomain()
        }
    }
}