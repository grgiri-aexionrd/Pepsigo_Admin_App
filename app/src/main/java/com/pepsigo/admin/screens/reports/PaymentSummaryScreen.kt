package com.pepsigo.admin.screens.reports

import android.os.Build
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pepsigo.admin.R
import com.pepsigo.admin.model.PaymentSummaryItem
import com.pepsigo.admin.model.PaymentSummaryResponse
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSummaryScreen(
    viewModel: PaymentSummaryViewModel ,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val refreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.payment_summary),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.payment_summary),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = false,
            onRefresh = { viewModel.fetch(state.fromDate, state.toDate) },
            modifier = Modifier
                .fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(
//                        color = inversePrimaryLight.copy(alpha = 0.35f)
//                    )
                    .padding(innerPadding)
                    .padding(16.dp)
            ){
                stickyHeader {
                    PaymentTitleCard(
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        error = state.isDateError,
                        onFromDateSelected = { viewModel.setFromDate(it) },
                        onToDateSelected = { viewModel.setToDate(it) },
                        onGetDetailsClicked = { viewModel.fetch(state.fromDate, state.toDate) }
                    )

                    PaymentTableHeader()
                }

                if (state.isError) {
                    item {
                        Text(
                            text = state.snackbarMessage ?: "Error",
                            modifier = Modifier.align(Alignment.Center)
                                .padding(12.dp)
                        )
                    }
                }

                // table items
                items(state.data?.data ?: emptyList()) { data ->
                    PaymentTableRow(
                        paymentData = data
                    )
                }
                item {
                    PaymentTotalStickyFooter(
                        totalAmount = state.footerAmount,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }
                if (state.isLoading) {
                    // full-screen transparent loading indicator centered in the viewport
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PaymentTitleCard(
    fromDate: String ,
    toDate: String ,
    error: Boolean = false,
    onFromDateSelected: (String) -> Unit,
    onToDateSelected: (String) -> Unit,
    onGetDetailsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
//                    .background(color = MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InlineDateRangePicker(
            label = "Date",
            fromDate = fromDate,
            toDate = toDate,
            error = error,
            onFromDateSelected = { onFromDateSelected(it)},
            onToDateSelected = { onToDateSelected(it)}
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (error) {
            Text(
                text = "Please select a valid date range",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Button(
            onClick = onGetDetailsClicked,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = "Get Details")
        }

    }
}

@Composable
fun PaymentTableHeader() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Payment Method",
//                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Txn. Count",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Txn. Amount",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun PaymentTableRow(
    paymentData: PaymentSummaryItem
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = paymentData.paymentMethod,
            modifier = Modifier.weight(0.4f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start
        )
        Text(text = paymentData.txnCount.toString(),
            modifier = Modifier.weight(0.2f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Text(text = "₹ " + paymentData.totalAmount,
            modifier = Modifier.weight(0.3f),
//            modifier = Modifier.wrapContentWidth(Alignment.End),
            style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.End)
    }
}

@Composable
fun PaymentTotalStickyFooter(
    totalAmount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Empty space for Payment Method column
            Spacer(modifier = Modifier.weight(0.4f))

            // Empty space for Txn Count column
            Spacer(modifier = Modifier.weight(0.2f))

            // Total Amount (aligned)
            Text(
//                text = "₹ %.2f".format(totalAmount),
                text = totalAmount,
                modifier = Modifier.weight(0.3f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}






