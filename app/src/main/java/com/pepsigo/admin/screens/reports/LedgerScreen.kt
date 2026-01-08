package com.pepsigo.admin.screens.reports

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.repository.TransactionDetailUi
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LedgerScreen(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.ledger.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Customer Ledger", "Vendor Ledger")
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.cust_vendor_ledger),
                icon = Icons.Default.AccountBalance,
                desc = stringResource(R.string.cust_vendor_ledger),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface

    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {  viewModel.refresh()  },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        ){
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ){
                item {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Transparent
                    ) {
                        tabs.forEachIndexed { index, tabTitle ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        tabTitle, style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item{
                    when (selectedTab) {
                        0 -> CustomerLedger(
                            fromDate = state.customerFromDate,
                            toDate = state.customerToDate,
                            customerList = state.customers,
                            selectedCustomer = state.selectedCustomer,
                            onCustomerSelected = {  viewModel.updateSelectedCustomer(it) },
                            onGetCustomerLedger = { viewModel.getCustomerLedger(state.customerFromDate,state.customerToDate,it) },
                            customerError = state.customerError,
                            selectedCustomerError = state.selectedCustomerError,
                            onFromDatePicker = {  viewModel.setCustomerFromDate(it) },
                            onToDatePicker = {  viewModel.setCustomerToDate(it) }
                        )
                        1-> VendorLedger(
                            fromDate = state.vendorFromDate,
                            toDate = state.vendorToDate,
                            vendorList = state.vendors,
                            selectedVendor = state.selectedVendor,
                            onVendorSelected = {  viewModel.updateSelectedVendor(it) },
                            onGetVendorLedger = { viewModel.getVendorLedger(state.vendorFromDate,state.vendorToDate,it) },
                            vendorError = state.vendorError,
                            selectedVendorError = state.selectedVendorError,
                            onFromDatePicker = {  viewModel.setVendorFromDate(it) },
                            onToDatePicker = {  viewModel.setVendorToDate(it) }

                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when (selectedTab) {
                    // Customer list
                    0 -> {
                        if (state.isLoading) {
                            Log.d("LedgerScreen", "Loading")
                            item {
                                ShowLoadingState()
                            }
                            return@LazyColumn
                        }

                        // 2️⃣ No customer selected (initial screen)
                        if (state.customerError ==null && state.selectedCustomer == null) {
                            Log.d("LedgerScreen", "No customer selected")
                            item {
                                DropDownSelectionEmpty(stringResource(R.string.dropdown_selection_empty))
                            }
                            return@LazyColumn
                        }
                        if (state.snackbarMessage != null) {
                            Log.d("LedgerScreen", "Snackbar message: ${state.snackbarMessage}")
                            item {
                                ErrorState(state.snackbarMessage!!)
                            }
                            return@LazyColumn
                        }
                        item{
                            Row(){
                                Icon(Icons.Default.FilterAlt, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(text = "${state.customerLedger.size} Transactions Found")
                            }
                        }
                        if (state.hasCustomerSearched && state.customerLedger.isEmpty()) {
                            item { EmptyState(stringResource(R.string.empty_dues_customer)) }
                            return@LazyColumn
                        }
                        items(
                            items = state.customerLedger,
                        ) { item ->
                            LedgerCard(item)
                        }

                    }

                    // vendor list
                    1 -> {
                        if (state.isLoading) {
                            item {
                                ShowLoadingState()
                            }
                            return@LazyColumn

                        }
                        if (state.vendorError == null && state.selectedVendor == null) {
                            item {
                                DropDownSelectionEmpty(stringResource(R.string.dropdown_selection_empty))
                            }
                            return@LazyColumn
                        }
                        if (state.snackbarMessage != null) {
                            item {
                                ErrorState(state.snackbarMessage!!)
                            }
                            return@LazyColumn
                        }
                        item{
                            Row(){
                                Icon(Icons.Default.FilterAlt, contentDescription = "")
                                Spacer(Modifier.width(8.dp))
                                Text(text = "${state.vendorLedger.size} Transactions Found")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if ( state.hasVendorSearched && state.vendorLedger.isEmpty()) {
                            item { EmptyState(stringResource(R.string.empty_dues_vendor)) }
                            return@LazyColumn
                        }

                        items(
                            items = state.vendorLedger,
                        ) { item ->
                            LedgerCard(item)
                        }
                    }
                }

            }

        }
    }

}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CustomerLedger(
    fromDate:String,
    toDate:String,
    onFromDatePicker: (String) -> Unit,
    onToDatePicker: (String) -> Unit,
    customerList: List<DropDownList>,
    selectedCustomer: DropDownList?,
    onCustomerSelected: (DropDownList?) -> Unit,
    onGetCustomerLedger: (Int?) -> Unit,
    customerError: String? = null,
    selectedCustomerError: Boolean = false,
){
    if(customerError != null) {
        DropDownErrorCard(error = customerError)
    } else{
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
//                    .background(color = MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InlineDateRangePicker(
                    label = "Date Range",
                    fromDate = fromDate,
                    toDate = toDate,
                    onFromDateSelected = { onFromDatePicker(it)},
                    onToDateSelected = { onToDatePicker(it)}
                )

                DropDown(
                    dropDown = customerList,
                    error = selectedCustomerError,
                    label = stringResource(R.string.select_customer),
                    selected = selectedCustomer,
                    labelExtractor = { it.name },
                    onSelected = { onCustomerSelected(it) },
                    isCreatePurchase = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onGetCustomerLedger(selectedCustomer?.id) },
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                ) {
                    Text(text = "Get Details")
                }
//                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VendorLedger(
    fromDate:String,
    toDate:String,
    onFromDatePicker: (String) -> Unit,
    onToDatePicker: (String) -> Unit,
    vendorList: List<DropDownList>,
    selectedVendor: DropDownList?,
    onVendorSelected: (DropDownList?) -> Unit,
    onGetVendorLedger: (Int?) -> Unit,
    vendorError: String? = null,
    selectedVendorError: Boolean = false,
){
    if(vendorError != null) {
        DropDownErrorCard(error = vendorError)
    } else{
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
//                    .background(color = MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                InlineDateRangePicker(
                    label = "Date Range",
                    fromDate = fromDate,
                    toDate = toDate,
                    onFromDateSelected = { onFromDatePicker(it)},
                    onToDateSelected = { onToDatePicker(it)}
                )
                DropDown(
                    dropDown = vendorList,
                    error = selectedVendorError,
                    label = stringResource(R.string.select_vendor),
                    selected = selectedVendor,
                    labelExtractor = { it.name },
                    onSelected = { onVendorSelected(it) },
                    isCreatePurchase = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onGetVendorLedger(selectedVendor?.id) },
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                ) {
                    Text(text = "Get Details")
                }

            }

        }

    }

}

@Composable
fun LedgerCard(
    item: TransactionDetailUi
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ){
            Icon(if(item.type == "Purchase" || item.type == "Invoice")Icons.Default.Receipt else Icons.Default.Payment,
                contentDescription = "",
               tint = if(item.type == "Purchase" || item.type == "Invoice") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if(item.type == "Purchase" || item.type == "Invoice") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(text = item.type, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text(text = item.ref ,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = item.date,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.End

             ) {
                 Text(
                     text = item.amount,
                     color = if (item.type == "Purchase" || item.type == "Invoice") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                     style = MaterialTheme.typography.titleMedium
                 )
             }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

}