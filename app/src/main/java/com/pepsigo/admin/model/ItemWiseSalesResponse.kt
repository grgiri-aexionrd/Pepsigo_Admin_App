package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class ItemWiseSalesResponse(
    val count:Int,
    val data:List<ItemWiseSalesData> = emptyList()
)
data class ItemWiseSalesData(
    @SerializedName("item_id")
    val itemId:Int,
    @SerializedName("item_name")
    val itemName:String,
    val unit:String,
    @SerializedName("total_qty")
    val totalQty:String,
    @SerializedName("total_sales")
    val totalSales:String
)