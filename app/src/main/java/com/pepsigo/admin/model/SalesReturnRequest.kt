package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class SalesReturnRequest(
    val items: List<SalesReturnItems>
)

data class SalesReturnItems(
    @SerializedName("inventory_id")
    val invId: Int,
    val quantity: Int
)
