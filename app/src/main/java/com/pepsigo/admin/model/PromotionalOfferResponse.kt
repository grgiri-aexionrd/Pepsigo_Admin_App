package com.pepsigo.admin.model

data class PromotionalOfferResponse<T>(
    val data: T? = null,
    val count: Int
)
