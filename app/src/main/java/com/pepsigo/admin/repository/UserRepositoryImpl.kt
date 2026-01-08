package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.EditUserRequest
import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError


class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {

    override suspend fun getUsers(role: String): Result<List<User>> {
        return wrapError {
            val response = apiService.getUsers(role)
            Log.d("UserRepositoryImpl", "Received response: $response")
            response.map { it.toDomain() }

        }
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return wrapError {
            val response = apiService.getUserById(id)
            response.toDomain()
        }
    }


    override suspend fun addCustomer(form: AddUserRequest): Result<User> {
        return wrapError {
            val response = apiService.addCustomer(form)
            response.user.toDomain()
        }

    }

    override suspend fun addVendor(form: AddUserRequest): Result<UserSuccessResponse<User>> {
        return wrapError {
            Log.d("UserRepositoryImpl", "Adding vendor with form: $form")

            val response = apiService.addVendor(form)
            Log.d("UserRepositoryImpl", "Received response: $response")

            UserSuccessResponse(
                message = response.message,
                data = response.user.toDomain()
            )
        }
    }

    override suspend fun updateUser(form: EditUserRequest): Result<UserSuccessResponse<User>> {
        return wrapError {
            val response = apiService.updateUser(form.id, form.userDetail)
            UserSuccessResponse(
                message = response.message,
                data = response.user.toDomain()
            )
        }

    }

    override suspend fun toggleUserStatus(id: Int): Result<UserSuccessResponse<User>> {
        return wrapError {
            val response = apiService.toggleUser(id)
            UserSuccessResponse(
                message = response.message,
                data = response.user.toDomain()
            )
        }
    }

}