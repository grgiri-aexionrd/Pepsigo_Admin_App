package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class CreatePurchaseRequest(
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String?,
    @SerializedName("purchase_date")
    val purchaseDate: String,
    val items: List<PurchaseItem>
)

data class PurchaseItem(
    @SerializedName("inventory_id")
    val inventoryId: Int,
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
    @SerializedName("expiry_date")
    val expiryDate: String?
)


