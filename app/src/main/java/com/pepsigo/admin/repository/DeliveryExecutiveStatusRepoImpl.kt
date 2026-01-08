package com.pepsigo.admin.repository

import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.mapper.toDto
import com.pepsigo.admin.model.ExecutiveStatus
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.screens.deliveryExecutive.NewDelForm
import com.pepsigo.admin.utils.wrapError

class DeliveryExecutiveStatusRepoImpl( private val apiService: ApiService): DeliveryExecutiveStatusRepo {
    override suspend fun fetchDeliveryExecutiveStatuses(): Result<List<ExecutiveStatus>> {
        return wrapError {
            val response = apiService.getDeliveryExecutivesStatus()
            response.data.map { it.toDomain() }
        }
    }

    override suspend fun addDeliveryExec( form: NewDelForm): Result<UserSuccessResponse<User>>  {
        return wrapError{
            val dto = form.toDto()
            val response = apiService.addDeliveryExecutive(dto)
            UserSuccessResponse(
                message = response.message,
                data = response.user.toDomain()
            )
        }
    }

}
