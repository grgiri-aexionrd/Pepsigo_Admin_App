package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class OutstandingPayableResponse(
    val count:Int,
    val data: List<VendorDues>
)

data class VendorDues (
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("vendor_name")
    val vendorName: String,
    @SerializedName("total_purchases")
    val totalPurchases: Double,
    @SerializedName("payment_made")
    val paymentReceived: Double,
    @SerializedName("outstanding_balance")
    val outstandingDues: Double
)