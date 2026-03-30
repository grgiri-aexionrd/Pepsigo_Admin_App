package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class CreateSaleRequestDto (
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("route_assignment_id") val routeAssignmentId: Int? = null,
    @SerializedName("sale_date") val saleDate: String,
    @SerializedName("items") val items: List<CreateSaleItemDto>
)

data class CreateSaleItemDto (
    @SerializedName("inventory_id") val inventoryId: Int,
    @SerializedName("batch_number") val batchNumber: Int,
    @SerializedName("item_quantity") val itemQuantity: Int,
    @SerializedName("item_unit") val itemUnit: String
)




