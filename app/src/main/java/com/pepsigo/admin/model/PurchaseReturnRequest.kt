package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchaseReturnRequest(
    val items: List<ReturnItems>
)
data class ReturnItems(
    @SerializedName("inventory_id")
    val invId: Int,
    val quantity: Int
)