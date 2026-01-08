package com.pepsigo.admin.model

data class UserSuccessResponse<T>(
    val message: String,
    val data: T
)
