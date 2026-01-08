package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class OutstandingReceivablesResponse (
    val count:Int,
    val data: List<CustomerDues>
)

data class CustomerDues (
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("total_sales")
    val totalSales: Double,
    @SerializedName("payment_received")
    val paymentReceived: Double,
    @SerializedName("outstanding_balance")
    val outstandingDues: Double
)