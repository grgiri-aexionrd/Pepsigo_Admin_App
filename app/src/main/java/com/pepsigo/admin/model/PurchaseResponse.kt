package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchaseResponse<T>(
    val message: String? =null,
    val data: T? = null,
    val details: String? = null,
    @SerializedName("has_sales")
    val hasSales: Boolean? = null
)