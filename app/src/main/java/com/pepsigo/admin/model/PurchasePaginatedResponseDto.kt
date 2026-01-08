package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchasePaginatedResponseDto (
    @SerializedName("current_page")
    val currentPage: Int,
    val data : List<PurchaseDto>,
    @SerializedName("first_page_url")
    val firstPageUrl: String,
    @SerializedName("from")
    val from: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("last_page_url")
    val lastPageUrl: String,
    val links: List<PageLink>,
    @SerializedName("next_page_url")
    val nextPageUrl: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("prev_page_url")
    val prevPageUrl: String,
    @SerializedName("to")
    val to: Int,
    @SerializedName("total")
    val total: Int
)

data class PurchaseDto(
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
    val vendor: UserDto

)

data class PageLink(
    val url: String,
    val label: String,
    val page: Int,
    val active: Boolean
)

