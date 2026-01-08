package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class DashboardDto(
    val metrics: MetricsDto,
    val users: DashboardUserDto
)

data class MetricsDto(
    @SerializedName("total_sales_today")
    val totalSalesToday: Double,
    @SerializedName("total_sale_qty_today")
    val totalSaleQtyToday: Int,
    @SerializedName("total_sales_count_today")
    val totalSalesCountToday: Int,
    @SerializedName("total_purchases_today")
    val totalPurchasesToday: Double,
    @SerializedName("payments_received_today")
    val paymentsReceivedToday: Double,
    @SerializedName("outstanding_receivables")
    val outstandingReceivables: Double,
    @SerializedName("outstanding_payables")
    val outstandingPayables: Double,
    @SerializedName("inventory_items")
    val inventoryItems: Int,
    @SerializedName("low_stock_items")
    val lowStockItems: Int,
    @SerializedName("total_customers")
    val totalCustomers: Int,
    @SerializedName("total_delivery_boys")
    val totalDeliveryBoys: Int,
    @SerializedName("total_admins")
    val totalAdmins: Int
)

data class DashboardUserDto(
    val admins: List<AdminLocationDto>,
    val customers: List<CustomerLocationDto>,
    @SerializedName("delivery_executives")
    val deliveryExecutives: List<DeliveryExecutiveLocationDto>
)

data class AdminLocationDto(
    val id: Int,
    val name: String,
    val role: String,
    val latitude: Double?,
    val longitude: Double?
)

data class CustomerLocationDto(
    val id: Int,
    val name: String,
    @SerializedName("business_name")
    val businessName: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class DeliveryExecutiveLocationDto(
    val id: Int,
    val name: String,
    val role: String,
    @SerializedName("route_status")
    val routeStatus: String,
    val latitude: Double?,
    val longitude: Double?
)