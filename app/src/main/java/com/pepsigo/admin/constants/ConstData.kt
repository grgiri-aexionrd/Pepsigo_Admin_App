package com.pepsigo.admin.constants


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.text.input.KeyboardType
import com.pepsigo.admin.R
import com.pepsigo.admin.model.DrawerGroup
import com.pepsigo.admin.model.DrawerItem
import com.pepsigo.admin.model.ReportItem
import com.pepsigo.admin.model.UserForm


val modalDrawerGroups = listOf(
    DrawerGroup(
        "Transaction",
        listOf(
            DrawerItem("Sales", "sales", Icons.Default.PointOfSale),
            DrawerItem("Payment", "payment", Icons.Default.Payment),
        )
    ),
    DrawerGroup(
        "Operations",
        listOf(
            DrawerItem("Location", "location", Icons.Default.Place),
            DrawerItem("Routes", "routes", Icons.Default.Map),
            DrawerItem("DeliveryExecutives", "delivery_executives", Icons.Default.People)

        )
    ),
    DrawerGroup(
        "Inventory",
        listOf(
            DrawerItem("Inventory", "inventory", Icons.Default.Inventory),
            DrawerItem("Purchase", "purchase", Icons.Default.ShoppingCart),
            DrawerItem("Vendors", "vendors", Icons.Default.Store)
        )
    ),
    DrawerGroup(
        "Customers & Marketing",
        listOf(
            DrawerItem("Customers", "customers", Icons.Default.People),
            DrawerItem("Promotions", "promotions", Icons.Default.Campaign),
        )
    ),
    DrawerGroup(
        "Analytics",
        listOf(
            DrawerItem("Reports", "reports", Icons.Default.Assessment)
        )
    )
)

val UserFormFields = listOf(
    FormFieldDescriptor(UserForm::name, " Name"),
    FormFieldDescriptor(UserForm::mobile, "Mobile Number", KeyboardType.Phone),
    FormFieldDescriptor(UserForm::address1, "Address 1"),
    FormFieldDescriptor(UserForm::address2, "Address 2"),
    FormFieldDescriptor(UserForm::state, "State"),
    FormFieldDescriptor(UserForm::pincode, "Pincode", KeyboardType.Number)

)

val reports = listOf(
    ReportItem(R.string.sales_register, Routes.SalesRegister),
    ReportItem(R.string.purchase_register, Routes.PurchaseRegister),
    ReportItem(R.string.outstanding_dues, Routes.OutstandingDues),
    ReportItem(R.string.stock_summary, Routes.StockSummary),
    ReportItem(R.string.batch_stock, Routes.BatchStock),
    ReportItem(R.string.ledger, Routes.Ledger),
    ReportItem(R.string.item_wise_delivery, Routes.ItemWiseDelivery),
    ReportItem(R.string.item_wise_sales, Routes.ItemWiseSales),
    ReportItem(R.string.payment_summary, Routes.PaymentSummary)


)

val unitList = listOf("Pcs", "Box", "Case", "Bundle")
