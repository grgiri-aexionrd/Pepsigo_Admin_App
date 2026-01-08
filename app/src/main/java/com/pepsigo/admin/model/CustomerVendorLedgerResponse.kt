package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class CustomerLedgerResponse(
    @SerializedName("customer_id")
    val customerId: Int,
    val entries : List<TransactionDetail>,
    val balance: Double

)

data class TransactionDetail(
    val type: String,
    val date: String,
    val ref: String?, // can be null
    val amount: Double,
)

data class VendorLedgerResponse(
    @SerializedName("vendor_id")
    val vendorId: Int,
    val entries : List<TransactionDetail>,
    val balance: Double
)

