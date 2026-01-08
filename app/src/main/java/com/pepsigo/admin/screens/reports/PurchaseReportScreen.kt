package com.pepsigo.admin.screens.reports

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.DockedDatePicker
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.NoDataCard
import com.pepsigo.admin.screens.commonComponents.ReportCard
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun PurchaseReportScreen(
    viewModel: PurchaseReportViewModel,
    onNavigateBack: () -> Unit
) {
    val purchaseReport by viewModel.purchaseReport.collectAsState()

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.purchase_register),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.purchase_register),
                onBackClick = { onNavigateBack() }
            )
        }
    ){innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
//                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(16.dp)

                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // minimal elevation for separation
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),

                    ) {
                    Text(
                        text = stringResource(R.string.from_date),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    DockedDatePicker(
                        label = stringResource(R.string.select_date),
                        error = purchaseReport.fromDateError,
                        modifier = Modifier.fillMaxWidth(),
                        onDateSelected = { date -> viewModel.setFromDate(date) }
                    )
                    Text(
                        text = stringResource(R.string.to_date),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    DockedDatePicker(
                        label = stringResource(R.string.select_date),
                        error = purchaseReport.toDateError,
                        modifier = Modifier.fillMaxWidth(),
                        onDateSelected = { date -> viewModel.setToDate(date) }
                    )
                    Text(
                        text = stringResource(R.string.vendor_dropdown),
                        style = MaterialTheme.typography.bodyLarge,

                        )
                    Log.d(
                        "PurchaseReportScreen",
                        "Rendering DropDown with selected: ${purchaseReport.selected}, options: ${purchaseReport.dropDown}"
                    )

                    DropDown(
                        dropDown = purchaseReport.dropDown,
                        error = purchaseReport.dropDownError,
                        label = stringResource(R.string.select_vendor),
                        selected = purchaseReport.selected,
                        labelExtractor = {it.name},
                        onSelected = { viewModel.updateSelected(it) }
                    )

                    Button(
                        onClick = {
                            Log.d(
                                "PurchaseReportScreen",
                                "Apply Filter clicked with fromDate: ${purchaseReport.fromDate}, toDate: ${purchaseReport.toDate}, selected customer: ${purchaseReport.selected}"
                            )
                            // Trigger filter action in ViewModel
                            viewModel.fetchPurchaseRegister(
                                dateFrom = purchaseReport.fromDate,
                                dateTo = purchaseReport.toDate,
                                vendorId = purchaseReport.selected?.id
                            )

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !purchaseReport.isLoading
                    ) {
                        if (purchaseReport.isLoading) {
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
                //             display purchase report data here based on purchaseReport state
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ){
                    if (purchaseReport.report.isEmpty()) {

                        item {
                            NoDataCard(message = stringResource(R.string.no_sales_data))
                        }
                    } else {

                        items(purchaseReport.report) { reportItem ->
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
                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }
                }
            }
        }

    }



}