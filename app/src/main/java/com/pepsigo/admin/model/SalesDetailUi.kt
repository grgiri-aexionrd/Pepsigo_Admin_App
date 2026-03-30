package com.pepsigo.admin.model

data class SalesDetailUi (
    val sales: SalesUi,
    val customer: User,
    val amountSummary: AmountSummaryUi,
    val salesItems: List<SalesItemsDetailUi> = emptyList(),
)

data class SalesUi(
    val salesId : Int,
    val invoiceNumber: String,
    val saleDate: String,
    val invoiceStatus: String
)

data class SalesItemsDetailUi(
    val id: Int,
    val saleId: Int,
    val inventoryId: Int,
    val batchNumber: Int,
    val itemQuantity: String,
    val unit: String,
    val gstPercent: String,
    val costPrice: String,
    val salePrice: String,
    val retailPrice: String,
    val totalAmount: String,
    val inventory: InventoryUi,
    val batch: SaleBatchUi,
)

data class SaleBatchUi(
    val id: Int,
    val invoiceNumber: String?,
    val vendorId: Int,
    val purchaseDate: String,
    val subTotal: String,
    val discountBt: String,
    val taxAmount: String,
    val discountAt: String,
    val totalAmount: String,
    val invoiceStatus: String,
)



