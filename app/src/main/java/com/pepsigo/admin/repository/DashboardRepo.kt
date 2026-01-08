package com.pepsigo.admin.repository

import com.pepsigo.admin.model.DashboardData

interface DashboardRepo {
    suspend fun fetchDashboardData(): Result<DashboardData>
}