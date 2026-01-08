package com.pepsigo.admin.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import com.pepsigo.admin.model.Admin
import com.pepsigo.admin.model.Customer
import com.pepsigo.admin.model.DashboardUsers
import com.pepsigo.admin.model.DeliveryExecutive
import com.pepsigo.admin.model.MapSectionState
import com.pepsigo.admin.model.MetricCardState
import com.pepsigo.admin.model.MetricsData
import com.pepsigo.admin.model.UserLocationUI

fun MetricsData.toSalesUi(): List<MetricCardState> {
    return listOf(
        MetricCardState.Loaded("Sales Today", totalSalesToday.toString(), Icons.Default.ShoppingCart, Color.Green),
        MetricCardState.Loaded("Qty Sold", totalSaleQtyToday.toString(), Icons.Default.LocalOffer, Color.Green),
        MetricCardState.Loaded("Sales Count", totalSalesCountToday.toString(),Icons.AutoMirrored.Filled.List, Color.Green),
        MetricCardState.Loaded("Payments Received", paymentsReceivedToday.toString(), Icons.Default.AccountBalanceWallet, Color.Green),
        MetricCardState.Loaded("Receivables", outstandingReceivables.toString(), Icons.Default.CurrencyRupee, Color.Green),
        MetricCardState.Loaded("Payables", outstandingPayables.toString(), Icons.Default.CurrencyRupee, Color.Green)
    )
}

fun MetricsData.toInventoryUi(): List<MetricCardState> {
    return listOf(
        MetricCardState.Loaded("Inventory Items", inventoryItems.toString(), Icons.Default.Inventory2, Color.Blue),
        MetricCardState.Loaded("Low Stock", lowStockItems.toString(), Icons.Default.Warning, Color.Blue),
        MetricCardState.Loaded("Purchases Today", totalPurchasesToday.toString(), Icons.Default.ShoppingBag, Color.Blue)
    )
}

fun MetricsData.toUsersUi(): List<MetricCardState> {
    return listOf(
        MetricCardState.Loaded("Customers", totalCustomers.toString(), Icons.Default.Person, Color.Yellow),
        MetricCardState.Loaded("Delivery Boys", totalDeliveryBoys.toString(), Icons.Default.DeliveryDining, Color.Yellow),
        MetricCardState.Loaded("Admins", totalAdmins.toString(), Icons.Default.AdminPanelSettings, Color.Yellow)
    )
}

fun Admin.toUi(): UserLocationUI? {
    if (latitude == null || longitude == null) return null
    return UserLocationUI(
        id = id,
        title = name,
        lat = latitude ,
        lng = longitude ,
        category = role
    )
}

//@Composable
fun Customer.toUi(): UserLocationUI? {
    if (latitude == null || longitude == null) return null
    return UserLocationUI(
        id = id,
        title = name,
        subtitle = businessName,
        lat = latitude,
        lng = longitude,
        category = "customer"     //stringResource(R.string.customer)
    )
}

fun DeliveryExecutive.toUi(): UserLocationUI? {
    if (latitude == null || longitude == null) return null
    return UserLocationUI(
        id = id,
        title = name,
        subtitle = routeStatus,
        lat = latitude,
        lng = longitude,
        category = role
    )
}

fun DashboardUsers.toMapUi(): MapSectionState {
    return MapSectionState.Loaded(
        admins = admins.mapNotNull { it.toUi() },
        customers = customers.mapNotNull { it.toUi() },
        deliveryExecutives = deliveryExecutives.mapNotNull { it.toUi() }
    )
}