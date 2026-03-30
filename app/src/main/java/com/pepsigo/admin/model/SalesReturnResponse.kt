package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class SalesReturnResponse(
    val id: Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("route_assignment_id")
    val routeAssignmentId: Int?,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("made_by_user_id")
    val deliveryBoyId: Int,
    @SerializedName("sale_date")
    val saleDate: String,
    @SerializedName("sub_total")
    val subTotal: Double,
    @SerializedName("discount_BT")
    val discountBt: Double,
    @SerializedName("tax_amount")
    val taxAmount: Double,
    @SerializedName("discount_AT")
    val discountAt: Double,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("invoice_status")
    val invoiceStatus: String,
    val items: List<SaleItemsReturnDto>,
)

data class SaleItemsReturnDto(
    val id: Int,
    @SerializedName("sale_id")
    val saleId: Int,
    @SerializedName("inventory_id")
    val inventoryId: Int,
    @SerializedName("batch_number")
    val batchId: Int,
    @SerializedName("item_quantity")
    val itemQuantity: Int,
    @SerializedName("item_unit")
    val unit: String,
    @SerializedName("item_gst_percent")
    val gstPercent: Double,
    @SerializedName("item_cost_price")
    val costPrice: Double,
    @SerializedName("item_sale_price")
    val salePrice: Double,
    @SerializedName("item_retail_price")
    val retailPrice: Double,
    @SerializedName("item_total_amount")
    val itemTotalAmount: Double,
    val inventory: ItemDto,
)
