package com.pepsigo.admin.model

data class InventoryResponse<T>(
    val message: String? = null,
    val data: T? = null,
    val details: String? = null,
    val error: String? = null
)
