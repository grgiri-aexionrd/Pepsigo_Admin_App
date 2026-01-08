package com.pepsigo.admin.model

data class LocationResponse<T>(
    val message: String? = null,
    val data: T? = null,
    val count: Int? = null,
    val error: String? = null
)
