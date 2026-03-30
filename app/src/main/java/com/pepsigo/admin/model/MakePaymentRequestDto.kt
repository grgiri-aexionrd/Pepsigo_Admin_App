package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class MakePaymentRequestDto (
    @SerializedName("sale_id") val saleId: Int? = null,
    @SerializedName("purchase_id") val purchaseId: Int? = null,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("transaction_type") val transactionType: String,
    @SerializedName("ref_number") val refNumber: String? = null,
    @SerializedName("denomination") val denomination: DenominationRequest?=null
)

