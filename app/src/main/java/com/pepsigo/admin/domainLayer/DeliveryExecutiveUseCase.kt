package com.pepsigo.admin.domainLayer

import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.DeliveryExecutiveUiModel
import com.pepsigo.admin.model.EditUserRequest
import com.pepsigo.admin.model.ExecutivesResult
import com.pepsigo.admin.model.RouteUi
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserForm
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.deliveryExecutive.NewDelForm
import com.pepsigo.admin.utils.AppError


class DeliveryExecutiveUseCase (
    private val userRepository: UserRepository,
    private val deliveryExecutiveRepository: DeliveryExecutiveStatusRepo
) {
    suspend operator fun invoke(): Result<ExecutivesResult> {
        val usersResult = userRepository.getUsers("Delivery Executive")
        if (usersResult.isFailure) return usersResult.map { ExecutivesResult(emptyList()) }

        val users = usersResult.getOrThrow()

        val deliveryResult = deliveryExecutiveRepository.fetchDeliveryExecutiveStatuses()
        return if (deliveryResult.isFailure) {
            val error = deliveryResult.exceptionOrNull() as? AppError
            val fallbackList = users.map { user ->
                DeliveryExecutiveUiModel(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    enabled = user.enabled,
                    status = "Unknown",
                    mobile = user.mobile,
                    route = null
                )
            }
            Result.success(ExecutivesResult(fallbackList, error))
        } else {
            val statuses = deliveryResult.getOrThrow()
            val combined = users.map { user ->
                val match = statuses.find { it.id == user.id }
                DeliveryExecutiveUiModel(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    enabled = user.enabled,
                    status = match?.status ?: "Unknown",
                    mobile = user.mobile,
                    route = match?.route?.let {
                        RouteUi(it.id, it.name, it.assignmentStatus)
                    }
                )
            }
            Result.success(ExecutivesResult(combined))
        }
    }

    suspend fun getDeliveryExecutiveById(id: Int): Result<User> {
        return userRepository.getUserById(id)
    }

    suspend fun addDeliveryExecutive(form: NewDelForm): Result<UserSuccessResponse<User>> {
        return deliveryExecutiveRepository.addDeliveryExec(form)

    }

    suspend fun updateDeliveryExecutive(form: UserForm): Result<UserSuccessResponse<User>> {
        return try {
            val request = EditUserRequest(
                id = requireNotNull(form.id) { "User ID must not be null for update" },
                userDetail = AddUserRequest(
                    name = form.name,
                    email = form.email,
                    mobile = form.mobile,
                    businessName = form.businessName,
                    locationId = form.locationId,
                    address1 = form.address1,
                    address2 = form.address2,
                    state = form.state,
                    pincode = form.pincode,
                    latitude = form.coordinates.split(",")[0].toDoubleOrNull(),
                    longitude = form.coordinates.split(",")[1].toDoubleOrNull(),
                )
            )
            val response = userRepository.updateUser(request)
             response
        }  catch (e: IllegalArgumentException) {
            Result.failure(AppError.Unknown(e.message ?: "Invalid data"))
        }

    }

    suspend fun toggleDeliveryExecutiveStatus(id: Int): Result<UserSuccessResponse<User>> {
        return userRepository.toggleUserStatus(id=id)
    }
}


