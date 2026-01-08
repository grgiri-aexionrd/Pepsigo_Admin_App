package com.pepsigo.admin.model

data class DashboardData(
    val metrics: MetricsData,
    val users: DashboardUsers
)

data class DashboardUsers(
    val admins: List<Admin>,
    val customers: List<Customer>,
    val deliveryExecutives: List<DeliveryExecutive>
)

data class MetricsData(
    val totalSalesToday: Double,
    val totalSaleQtyToday: Int,
    val totalSalesCountToday: Int,
    val totalPurchasesToday: Double,
    val paymentsReceivedToday: Double,
    val outstandingReceivables: Double,
    val outstandingPayables: Double,
    val inventoryItems: Int,
    val lowStockItems: Int,
    val totalCustomers: Int,
    val totalDeliveryBoys: Int,
    val totalAdmins: Int
)

data class Admin(
    val id: Int,
    val name: String,
    val role: String,
    val latitude: Double?,
    val longitude: Double?
)

data class Customer(
    val id: Int,
    val name: String,
    val businessName: String?,
    val latitude: Double?,
    val longitude: Double?
)
data class DeliveryExecutive(
    val id: Int,
    val name: String,
    val role: String,
    val routeStatus: String,
    val latitude: Double?,
    val longitude: Double?
)