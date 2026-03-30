package com.pepsigo.admin.model

data class SalesResponse<T>(
    val message: String? = null,
    val data: T? = null,
    val details: String?
)