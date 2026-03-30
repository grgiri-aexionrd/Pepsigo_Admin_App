package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PaymentUpdateDto (
    @SerializedName("id") val id: Int,
    @SerializedName("received_by_id") val receivedById: Int,
    @SerializedName("purchase_id") val purchaseId: Int?,
    @SerializedName("sale_id") val saleId: Int?,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("ref_number") val refNumber: String?,
    @SerializedName("transaction_type") val transactionType: String,
    @SerializedName("customer") val customer: UserDto?,
    @SerializedName("denomination") val denomination: DenominationDto?
)