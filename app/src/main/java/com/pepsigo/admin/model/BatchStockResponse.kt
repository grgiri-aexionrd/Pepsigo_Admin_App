package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class BatchStockResponse(
    val count: Int,
    val data : List<BatchStockItem>
)

data class BatchStockItem(
    @SerializedName("batch_id")
    val batchId: Int,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("expiry_date")
    val expiryDate: String?,
    @SerializedName("available_quantity")
    val availableQuantity: Int,
    val unit: String,
    @SerializedName("cost_price")
    val costPrice: Double,
    @SerializedName("sale_price")
    val salePrice: Double,

)