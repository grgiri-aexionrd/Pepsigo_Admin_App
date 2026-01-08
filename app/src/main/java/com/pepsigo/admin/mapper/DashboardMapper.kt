package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.Admin
import com.pepsigo.admin.model.AdminLocationDto
import com.pepsigo.admin.model.Customer
import com.pepsigo.admin.model.CustomerLocationDto
import com.pepsigo.admin.model.DashboardData
import com.pepsigo.admin.model.DashboardDto
import com.pepsigo.admin.model.DashboardUserDto
import com.pepsigo.admin.model.DashboardUsers
import com.pepsigo.admin.model.DeliveryExecutive
import com.pepsigo.admin.model.DeliveryExecutiveLocationDto
import com.pepsigo.admin.model.MetricsData
import com.pepsigo.admin.model.MetricsDto

// mapping DashboardDto to DashboardData
fun DashboardDto.toDomain(): DashboardData {

    return DashboardData(
    metrics = metrics.toDomain(),
    users = users.toDomain()
    )
}

// mapping MetricsDto to MetricsData
fun MetricsDto.toDomain() = MetricsData(
    totalSalesToday = totalSalesToday,
    totalSaleQtyToday = totalSaleQtyToday,
    totalSalesCountToday = totalSalesCountToday,
    totalPurchasesToday = totalPurchasesToday,
    paymentsReceivedToday = paymentsReceivedToday,
    outstandingReceivables = outstandingReceivables,
    outstandingPayables = outstandingPayables,
    inventoryItems = inventoryItems,
    lowStockItems = lowStockItems,
    totalCustomers = totalCustomers,
    totalDeliveryBoys = totalDeliveryBoys,
    totalAdmins = totalAdmins
)

// mapping DashboardUserDto to DashboardUsers
fun DashboardUserDto.toDomain() = DashboardUsers(
    admins = admins.map { it.toDomain() },
    customers = customers.map { it.toDomain() },
    deliveryExecutives = deliveryExecutives.map { it.toDomain() }
)

// mapping AdminLocationDto to Admin
fun AdminLocationDto.toDomain() = Admin(
    id = id,
    name = name,
    role = role,
    latitude = latitude,
    longitude = longitude
)

// mapping CustomerLocationDto to Customer
fun CustomerLocationDto.toDomain() = Customer(
    id = id,
    name = name,
    businessName = businessName,
    latitude = latitude,
    longitude = longitude
)

// mapping DeliveryExecutiveLocationDto to DeliveryExecutive
fun DeliveryExecutiveLocationDto.toDomain() = DeliveryExecutive(
    id = id,
    name = name,
    role = role,
    routeStatus = routeStatus,
    latitude = latitude,
    longitude = longitude
)