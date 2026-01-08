package com.pepsigo.admin.model

data class EditUserRequest(
    val id: Int,
    val userDetail: AddUserRequest,
)
