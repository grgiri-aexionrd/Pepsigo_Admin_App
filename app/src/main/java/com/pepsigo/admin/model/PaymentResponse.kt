package com.pepsigo.admin.model

data class PaymentResponse<T>(
    val message: String? =null,
    val data: T? = null,
    val details: String? = null,
)