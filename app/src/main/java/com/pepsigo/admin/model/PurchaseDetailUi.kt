package com.pepsigo.admin.model

data class PurchaseDetailUi(
    val purchase: PurchaseUi,
    val vendor: User,
    val amountSummary: AmountSummaryUi,
    val purchasedItems: List<ItemsDetailUi> = emptyList(),
    val hasSales: Boolean
)

data class PurchaseUi(
    val purchaseId : Int,
    val invoiceNumber: String,
    val purchaseDate: String,
    val invoiceStatus: String ,
)
data class AmountSummaryUi(
    val subTotal: String,       // e.g. "₹ 1,600.00"
    val discountBt: String,     // e.g. "₹ 0.00"
    val taxAmount: String,      // e.g. "₹ 800.00"
    val discountAt: String,     // e.g. "₹ 0.00"
    val totalAmount: String,    // e.g. "₹ 2,400.00"
)

data class ItemsDetailUi(
    val id: Int,
    val purchaseId: Int,
    val inventoryId: Int,
    val itemQuantity: String,
    val unit: String,
    val gstPercent: String,
    val costPrice: String,
    val salePrice: String,
    val retailPrice: String,
    val totalAmount: String,
    val expiryDate: String,
    val inventory: InventoryUi,
)

data class InventoryUi(
    val invId: Int,
    val name: String,
    val openingQuantity: String,
    val unit: String,
    val gstPercent: String,
)


