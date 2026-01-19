package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PaymentsPaginatedResponseDto(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<PaymentDto>,
    @SerializedName("first_page_url") val firstPageUrl: String?,
    @SerializedName("from") val from: Int?,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("last_page_url") val lastPageUrl: String?,
    @SerializedName("links") val links: List<PageLink>,
    @SerializedName("next_page_url") val nextPageUrl: String?,
    @SerializedName("path") val path: String,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("prev_page_url") val prevPageUrl: String?,
    @SerializedName("to") val to: Int?,
    @SerializedName("total") val total: Int
)

data class PaymentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("received_by_id") val receivedById: Int?,
    @SerializedName("purchase_id") val purchaseId: Int?,
    @SerializedName("sale_id") val saleId: Int?,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("ref_number") val refNumber: String?,
    @SerializedName("transaction_type") val transactionType: String,
    @SerializedName("customer") val customer: UserDto?,
    @SerializedName("received_by") val receivedBy: UserDto?,
    @SerializedName("sale") val sale: SaleDetailDto?,
    @SerializedName("purchase") val purchase: PurchaseSummaryDto?,
    @SerializedName("denomination") val denomination: DenominationDto?
)

data class SaleDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("invoice_number") val invoiceNumber: String?,
    @SerializedName("route_assignment_id") val routeAssignmentId: Int?,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("made_by_user_id") val madeByUserId: Int,
    @SerializedName("sale_date") val saleDate: String,
    @SerializedName("sub_total") val subTotal: Double,
    @SerializedName("discount_BT") val discountBT: Double,
    @SerializedName("tax_amount") val taxAmount: Double,
    @SerializedName("discount_AT") val discountAT: Double,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("invoice_status") val invoiceStatus: String
)

data class DenominationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("payment_id") val paymentId: Int,
    @SerializedName("denom_2000") val denom2000: Int,
    @SerializedName("denom_500") val denom500: Int,
    @SerializedName("denom_200") val denom200: Int,
    @SerializedName("denom_100") val denom100: Int,
    @SerializedName("denom_50") val denom50: Int,
    @SerializedName("denom_20") val denom20: Int,
    @SerializedName("denom_10") val denom10: Int,
    @SerializedName("denom_5") val denom5: Int,
    @SerializedName("denom_2") val denom2: Int,
    @SerializedName("denom_1") val denom1: Int,
    @SerializedName("card") val card: Int,
    @SerializedName("upi") val upi: Int,
    @SerializedName("net_banking") val netBanking: Int,
    @SerializedName("cheque") val cheque: Int,
    @SerializedName("credit") val credit: Int
)

data class PurchaseSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("invoice_number") val invoiceNumber: String?,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("purchase_date") val purchaseDate: String,
    @SerializedName("sub_total") val subTotal: Double,
    @SerializedName("discount_BT") val discountBt: Double,
    @SerializedName("tax_amount") val taxAmount: Double,
    @SerializedName("discount_AT") val discountAt: Double,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("invoice_status") val invoiceStatus: String
)