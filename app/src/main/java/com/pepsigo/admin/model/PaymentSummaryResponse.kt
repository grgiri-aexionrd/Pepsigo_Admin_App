package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PaymentSummaryResponse(
    val count: Int,
    val data: List<PaymentSummaryItem> = emptyList()
)

data class PaymentSummaryItem(
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("txn_count")
    val txnCount: Int,
    @SerializedName("total_amount")
    val totalAmount: String
)


