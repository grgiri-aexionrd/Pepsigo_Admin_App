package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PromotionalOfferCreate(
    @SerializedName("inventory_id")
    val inventoryId: Int,
    val quantity: Int,
    @SerializedName("auto_add")
    val autoAdd: Boolean,
    @SerializedName("sale_price")
    val salePrice: Double
)
