package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PromotionalOfferDto(
    @SerializedName("offer_id")
    val offerId:Int,
    @SerializedName("inventory_id")
    val inventoryId: Int,
    @SerializedName("item_name")
    val itemName : String,
    val quantity: Int,
    val unit : String,
    @SerializedName("auto_add")
    val autoAdd: Boolean,
    @SerializedName("sale_price")
    val salePrice: Double,
    @SerializedName("can_edit")
    val canEdit: Boolean
)



