package com.pepsigo.admin.repository

import com.pepsigo.admin.model.ExecutiveStatus
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.screens.deliveryExecutive.NewDelForm

interface DeliveryExecutiveStatusRepo {
    suspend fun fetchDeliveryExecutiveStatuses(): Result<List<ExecutiveStatus>>
    suspend fun addDeliveryExec(form: NewDelForm): Result<UserSuccessResponse<User>>
}