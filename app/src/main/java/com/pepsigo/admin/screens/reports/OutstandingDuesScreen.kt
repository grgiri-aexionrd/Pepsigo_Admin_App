package com.pepsigo.admin.screens.reports

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.model.VendorDuesUi
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar


@Composable
fun OutstandingDuesScreen(
    viewModel: OutstandingDuesViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.dues.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Customer Dues", "Vendor Dues")
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.outstanding_dues),
                icon = Icons.Default.RequestQuote,
                desc = stringResource(R.string.outstanding_dues),
                onBackClick = { onNavigateBack() }

            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface

    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        )
        {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
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

                item {
                    when (selectedTab) {
                        0 -> CustomerDropdown(
                            state,
                            onCustomerSelected = { viewModel.updateSelectedCustomer(it) },
                            onGetCustomerDues = { id ->
                                viewModel.getCustomerDues(id)
                            }
                        )

                        1 -> VendorDropdown(
                            state,
                            onVendorSelected = { viewModel.updateSelectedVendor(it) },
                            onGetVendorDues = { id ->
                                viewModel.getVendorDues(id)
                            }
                        )
                    }
                }

                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }



                when (selectedTab) {
                    // Customer list
                    0 -> {
                        if (state.isLoading) {
                            Log.d("OutstandingDuesScreen", "Loading")
                            item {
                                ShowLoadingState()
                            }

                            return@LazyColumn
                        }

                        // 2️⃣ No customer selected (initial screen)
                        if (state.selectedCustomer == null) {
                            Log.d("OutstandingDuesScreen", "No customer selected")
                            item {
                                DropDownSelectionEmpty(stringResource(R.string.dropdown_selection_empty))
                            }
                            return@LazyColumn
                        }


                        if (state.snackbarMessage != null) {
                            Log.d("OutstandingDuesScreen", "Snackbar message: ${state.snackbarMessage}")
                            item {
                                ErrorState(state.snackbarMessage!!)
                            }
                            return@LazyColumn
                        }

                        if (state.customerDues.isEmpty()) {
                            Log.d("OutstandingDuesScreen", "No dues recorded for this customer")
                            item { EmptyState(stringResource(R.string.empty_dues_customer)) }
                            return@LazyColumn
                        }

                        items(
                            items = state.customerDues,
                            key = { it.id }
                        ) { item ->
                            CustomerDuesCard(item)
                        }

                    }

                    // Vendor list
                    1 -> {
                        if (state.isLoading) {
                            item {
                                ShowLoadingState()
                            }
                            return@LazyColumn

                        }
                        // 2️⃣ No customer selected (initial screen)
                        if (state.selectedVendor == null) {
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

                        if (state.vendorDues.isEmpty()) {
                            item { EmptyState(stringResource(R.string.empty_dues_vendor)) }
                            return@LazyColumn
                        }

                        items(
                            items = state.vendorDues,
                            key = { it.id }
                        ) { item ->
                            VendorDuesCard(item)
                        }
                    }
                }

            }

        }

    }
}


@Composable
fun CustomerDropdown(
    state: OutstandingDuesUiState,
    onCustomerSelected: (DropDownList?) -> Unit,
    onGetCustomerDues: (Int?) -> Unit

) {
    if (state.customerError != null) {
        DropDownErrorCard(error = state.customerError)
    }else{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDown(
                dropDown = state.customers,
                error = state.selectedCustomerError,
                label = stringResource(R.string.select_customer),
                selected = state.selectedCustomer,
                labelExtractor = { it.name },
                onSelected = { onCustomerSelected(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick =  { onGetCustomerDues(state.selectedCustomer?.id) } ,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = "Apply Filter")
            }

        }

    }

}

@Composable
fun VendorDropdown(
    state: OutstandingDuesUiState,
    onVendorSelected: (DropDownList?) -> Unit,
    onGetVendorDues: (Int?) -> Unit
) {
    if (state.vendorError != null) {
        DropDownErrorCard(error = state.vendorError)
    }else{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDown(
                dropDown = state.vendors,
                error = state.selectedVendorError,
                label = stringResource(R.string.select_vendor),
                selected = state.selectedVendor,
                labelExtractor = { it.name },
                onSelected = { onVendorSelected(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onGetVendorDues(state.selectedVendor?.id) },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = "Apply Filter")
            }
        }

    }
}

@Composable
fun DropDownErrorCard(
    error: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text("Pull to refresh to try again",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

}

@Composable
fun CustomerDuesCard(
    item: CustomerDuesUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("${item.name} ",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("${item.due} ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error

                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Total Sale: ${item.totalSales} ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Paid: ${item.paid} ",
                    style = MaterialTheme.typography.bodyLarge
                )

            }

        }

    }



}

@Composable
fun VendorDuesCard(
    item: VendorDuesUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("${item.name} ",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("${item.balance} ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error

                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Total Purchase: ${item.totalPurchase} ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Balance: ${item.balance} ",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

    }
    Spacer(modifier = Modifier.height(16.dp))

}

@Composable
fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ShowLoadingState(){
    Box(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(20.dp)
//                .align(Alignment.CenterHorizontally as Alignment)
        )
    }
}

@Composable
fun DropDownSelectionEmpty(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        // "Select a customer to view dues"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lightbulb, contentDescription = "")
//            Spacer(modifier = Modifier.width(4.dp))
            Text(
                message,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


