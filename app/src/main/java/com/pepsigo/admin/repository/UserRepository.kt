package com.pepsigo.admin.repository


import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.EditUserRequest

import com.pepsigo.admin.model.User
import com.pepsigo.admin.model.UserSuccessResponse

interface UserRepository {
    suspend fun getUsers(role: String): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
    suspend fun addCustomer(form: AddUserRequest): Result<User>
    suspend fun addVendor(form: AddUserRequest): Result<UserSuccessResponse<User>>
    suspend fun updateUser( form: EditUserRequest): Result<UserSuccessResponse<User>>

    suspend fun toggleUserStatus(id: Int): Result<UserSuccessResponse<User>>
}