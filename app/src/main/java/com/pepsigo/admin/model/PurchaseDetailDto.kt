package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchaseDetailDto(
    val id: Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("purchase_date")
    val purchaseDate: String,
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
    val vendor: UserDto,
    val items: List<ItemsDetailDto>
)

data class ItemsDetailDto(
    val id: Int,
    @SerializedName("purchase_id")
    val purchaseId: Int,
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
    @SerializedName("item_total_amount")
    val totalAmount: Double,
    @SerializedName("expiry_date")
    val expiryDate: String,
    val inventory: ItemDto

    )

data class ItemDto(
    val id: Int,
    @SerializedName("item_name")
    val name: String,
    @SerializedName("opening_quantity")
    val openingQuantity: Int,
    val unit: String,
    @SerializedName("gst_percent")
    val gstPercent: Double,
    @SerializedName("is_enabled")
    val enabled: Boolean

)