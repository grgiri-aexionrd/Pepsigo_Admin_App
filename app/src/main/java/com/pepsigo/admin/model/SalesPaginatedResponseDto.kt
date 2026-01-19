package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class SalesPaginatedResponseDto(
    @SerializedName("current_page")
    val currentPage: Int,
    val data : List<SalesDto>,
    @SerializedName("first_page_url")
    val firstPageUrl: String?,
    @SerializedName("from")
    val from: Int?,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("last_page_url")
    val lastPageUrl: String?,
    val links: List<PageLink>,
    @SerializedName("next_page_url")
    val nextPageUrl: String?,
    @SerializedName("path")
    val path: String,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("prev_page_url")
    val prevPageUrl: String?,
    @SerializedName("to")
    val to: Int?,
    @SerializedName("total")
    val total: Int

)

data class SalesDto(
    val id: Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("route_assignment_id")
    val routeAssignmentId: Int?,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("made_by_user_id")
    val deliveryBoyId: Int,
    @SerializedName("sale_date")
    val saleDate: String,
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
    val customer: UserDto,
    @SerializedName("made_by")
    val deliveryBoy: UserDto,
    @SerializedName("route_assignment")
    val routeAssignment: RouteAssignmentDto?

)

data class RouteAssignmentDto(
    val id: Int,
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("delivery_executive_id")
    val deliveryBoyId: Int,
    val status: String,
)



