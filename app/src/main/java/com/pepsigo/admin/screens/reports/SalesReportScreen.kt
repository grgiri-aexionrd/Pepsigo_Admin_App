package com.pepsigo.admin.screens.reports

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.DockedDatePicker
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.NoDataCard
import com.pepsigo.admin.screens.commonComponents.ReportCard
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.screens.commonComponents.SearchDropDown

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SalesReportScreen(
    viewModel: SalesReportViewModel ,
    onNavigateBack: () -> Unit,
) {
    val salesReport by viewModel.salesReport.collectAsState()
    val refreshState = rememberPullToRefreshState()

    val filteredCustomers by remember {
        derivedStateOf {
            if (salesReport.searchQuery.isBlank()) {
                salesReport.dropDown.take(50)
            } else {
                salesReport.dropDown
                    .filter {
                        it.name.contains(salesReport.searchQuery, true)
                    }
                    .take(50)
            }
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.sales_register),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.sales_register),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = salesReport.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
//                .padding(top = 16.dp)
            ) {
                stickyHeader {
                    // datefilter, customer selection
                    FilterHeader(
                        fromDate = salesReport.fromDate,
                        toDate = salesReport.toDate,
                        error = salesReport.DateError,
                        onFromDateSelected = { viewModel.setFromDate(it) },
                        onToDateSelected = { viewModel.setToDate(it) },
                        dropDownLabel = stringResource(R.string.select_customer),
                        dropDown = filteredCustomers,
                        dropDownError = salesReport.dropDownError,
                        searchQuery = salesReport.searchQuery,
                        selected = salesReport.selected,
                        onSelected = { viewModel.updateSelected(it) },
                        onFetchClick = { dateFrom, dateTo, customerId ->
                            viewModel.fetchSalesRegister(dateFrom, dateTo, customerId)
                        },
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        isLoading = salesReport.isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // display sales report data here based on salesReport state

                if(salesReport.snackbarMessage != null){
                    item {
                        DropDownErrorCard(salesReport.snackbarMessage!!)
                    }
                }

                if ( salesReport.reportFetched &&  salesReport.report.isEmpty()) {
                    item {
                        NoDataCard(message = stringResource(R.string.no_sales_data))
                    }
                } else {
                    items(salesReport.report) { reportItem ->
                        ReportCard(
                            item = reportItem,
                            title = { it.invoiceNumber },
                            subtitle = { it.businessName },
                            status = { it.invoiceStatus },
                            dateValue = { it.saleDate },
                            amountValue = { "₹${it.totalAmount}" },
                            bottomItems = {
                                listOf(
                                    stringResource(R.string.sub_total) to "₹${it.subTotal}",
                                    stringResource(R.string.discount) to "₹${it.discountBeforeTax}",
                                    stringResource(R.string.tax) to "₹${it.tax}"
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun FilterHeader(
    fromDate: String,
    toDate: String,
    error: Boolean = false,
    onFromDateSelected: (String) -> Unit,
    onToDateSelected: (String) -> Unit,
    dropDown: List<DropDownList>,
    dropDownLabel: String,
    dropDownError: Boolean,
    searchQuery: String,
    selected: DropDownList?,
    onSelected: (DropDownList?) -> Unit,
    onSearchChange: (String) -> Unit,
    onFetchClick: (String,String,Int?) -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(8.dp),
//        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // minimal elevation for separation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InlineDateRangePicker(
                label = "Date",
                fromDate = fromDate,
                toDate = toDate,
                error = error,
                onFromDateSelected = { onFromDateSelected(it) },
                onToDateSelected = { onToDateSelected(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (error) {
                Text(
                    text = "Please select a valid date range",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
//            DropDown(
//                dropDown = dropDown,
//                error = dropDownError,
//                label = dropDownLabel,
//                selected = selected,
//                labelExtractor = { it.name },
//                onSelected = { onSelected(it) }
//            )
                //customer dropdown
                SearchDropDown(
                    filteredDropDown = dropDown,
                    error = dropDownError,
                    label = dropDownLabel,
                    searchQuery = searchQuery,
                    selected = selected,
                    onSelected = { onSelected(it) },
                    onSearchChange = { onSearchChange(it) },
                    labelExtractor = { it.name },
                )
        }
            Spacer(modifier = Modifier.height(8.dp))
            Button( onClick = {
                    onFetchClick(
                         fromDate,
                        toDate,
                        selected?.id
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Apply Filter", color = Color.White)
                }
            }
        }
    }
}