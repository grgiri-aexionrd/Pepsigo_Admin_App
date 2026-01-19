package com.pepsigo.admin.screens.reports

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.model.DeliveryPerformanceData
import com.pepsigo.admin.model.ExecutiveCollection
import com.pepsigo.admin.screens.commonComponents.ModalDatePicker
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DeliveryPerformanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: DeliveryPerformanceViewModel
) {
    val state by viewModel.uiState.collectAsState()

    val refreshState = rememberPullToRefreshState()
    val showPicker = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.delivery_performance),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.delivery_performance),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.fetch(state.fromDate, state.toDate) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(inversePrimaryLight.copy(alpha = 0.35f))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                stickyHeader {
                    PaymentTitleCard(
                        fromDate = state.fromDate,
                        toDate = state.toDate,
                        error = state.isDateError,
                        onFromDateSelected = { viewModel.setFromDate(it) },
                        onToDateSelected = { viewModel.setToDate(it) },
                        onGetDetailsClicked = { viewModel.fetch(state.fromDate, state.toDate) }
                    )
                    DeliveryTableHeader()
                }

                // rows
                val list = state.data?.data ?: emptyList()
                items(list) { exec ->
                    DeliveryTableRow(exec)
                }
                item{
                    PaymentTotalStickyFooter(
                        totalAmount = state.footerAmount,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                }

                // footer / loading overlay
                if (state.isLoading) {
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

                if (state.isError) {
                    item {
                        Text(text = state.snackbarMessage ?: "Error", modifier = Modifier.align(Alignment.Center)
                            .padding(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryTableHeader() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name",
                modifier = Modifier.weight(0.35f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Assigned",
                modifier = Modifier.weight(0.25f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Completed",
                modifier = Modifier.weight(0.25f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sales Total",
                modifier = Modifier.weight(0.3f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun DeliveryTableRow(exec: DeliveryPerformanceData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(0.35f), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = exec.executiveName,
                style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            text = exec.routesAssigned.toString(),
            modifier = Modifier.weight(0.25f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = exec.routesCompleted,
            modifier = Modifier.weight(0.25f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "₹${exec.salesTotal}",
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.Q)
//@Preview(showBackground = true)
//@Composable
//fun DeliveryPerformancePreview() {
//    // preview with sample data
//    val sample = DailyCollectionResponse(
//        date = "2026-01-06",
//        data = listOf(ExecutiveCollection(executiveId = 13, executiveName = "lufy3", routesAssigned = 2, routesCompleted = "2", salesTotal = "24719.95"))
//    )
//
//    val fakeVm = object : DeliveryPerformanceViewModel(previewRepo()) {}
//
//    // Can't easily preview ViewModel here due to constructor; keep a simple visual preview by drawing the table directly.
//}
//
//// small helper to satisfy preview ViewModel creation in a minimal way
//private fun previewRepo(): com.pepsigo.admin.repository.DailyCollectionRepo =
//    object : com.pepsigo.admin.repository.DailyCollectionRepo {
//        override suspend fun getDailyCollection(date: String): Result<DailyCollectionResponse> = Result.failure(Exception("not used"))
//        override suspend fun getPaymentSummary(from: String, to: String): Result<com.pepsigo.admin.model.PaymentSummaryResponse> = Result.failure(Exception("not used"))
//        override suspend fun getDeliveryPerformance(date: String): Result<DailyCollectionResponse> = Result.success(DailyCollectionResponse("2026-01-06", listOf(ExecutiveCollection(13, "lufy3", 2, "2", "24719.95"))))
//    }
