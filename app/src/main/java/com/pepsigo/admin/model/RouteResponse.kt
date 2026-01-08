package com.pepsigo.admin.model

data class RouteResponse<T>(
    val count: Int? = null,
    val data :T? = null,
    val message: String? = null,
)