package com.pepsigo.admin.screens.reports

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.DateRange
import com.pepsigo.admin.screens.commonComponents.ModalDatePicker
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.tooling.preview.Preview
import com.pepsigo.admin.R
import com.pepsigo.admin.constants.Routes
import com.pepsigo.admin.constants.reports
import com.pepsigo.admin.model.ReportItem
import com.pepsigo.admin.screens.commonComponents.ReportButton
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.pepsigo.admin.model.ExecutiveCollection
import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.model.PaymentSummaryResponse
import com.pepsigo.admin.ui.theme.inversePrimaryLight
import kotlin.text.ifEmpty



//private val previewVm = DailyCollectionViewModel(previewFakeRepo)

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: DailyCollectionViewModel
) {

    val dailyState by viewModel.uiState.collectAsState()

    // remember pull-to-refresh state (project helper with no params)
    val refreshState = rememberPullToRefreshState()
    // calendar icon + clickable date text
    val showPicker = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.reports_summary),
                icon = Icons.Default.Analytics,
                desc = stringResource(R.string.reports_summary),
                onBackClick = { onNavigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = dailyState.isLoading,
            onRefresh = { viewModel.fetch(dailyState.fromDate) },
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Single LazyColumn containing everything: title+grid, date row, header (sticky conditional), and rows
            val list = dailyState.data?.data ?: emptyList()
            val stickyThreshold = 6

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Title + grid card
                item {
                    TitleGridCard(onNavigate = onNavigate)
                }

                // Daily collections heading + date selector in one row
                item {
                    DailyCollectionHeader(
                        date = dailyState.fromDate,
                        dateState = dailyState.data?.date,
                        onDateSelected = { viewModel.setFromDate(it) },
                        showPicker = showPicker
                    )
                }

                // Header (sticky or normal depending on list size)
                if (list.size > stickyThreshold) {
                    stickyHeader {
                        TableHeader()
                    }
                } else {
                    item {
                        TableHeader()
                    }
                }

                // Table rows
                itemsIndexed(list) { idx, exec ->
                    DailyCollectionRow(exec)
                    if (idx < list.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // Loading / error as items
                if (dailyState.isLoading) {
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

                if (dailyState.isError) {
                    item {
//                        Text(text = dailyState.snackbarMessage ?: "Error", modifier = Modifier.padding(12.dp))
                        DropDownErrorCard(dailyState.snackbarMessage ?: "Error")
                    }
                }
            }

        }
    }
}


@Composable
private fun iconForReport(item: ReportItem): Pair<ImageVector, Color> {
    return when (item.route) {
        Routes.SalesRegister -> Pair(Icons.Default.ShoppingCart, MaterialTheme.colorScheme.primary)
        Routes.PurchaseRegister -> Pair(Icons.Default.Store, MaterialTheme.colorScheme.secondary)
        Routes.OutstandingDues -> Pair(Icons.Default.Campaign, MaterialTheme.colorScheme.error)
        Routes.StockSummary -> Pair(Icons.Default.Inventory, MaterialTheme.colorScheme.tertiary)
        Routes.BatchStock -> Pair(Icons.Default.Assessment, MaterialTheme.colorScheme.primaryContainer)
        Routes.Ledger -> Pair(Icons.Default.Assessment, MaterialTheme.colorScheme.secondaryContainer)
        // New mappings for the three added reports
        Routes.ItemWiseDelivery -> Pair(Icons.AutoMirrored.Filled.TrendingUp, MaterialTheme.colorScheme.primary)
        Routes.ItemWiseSales -> Pair(Icons.Default.BarChart, MaterialTheme.colorScheme.tertiary)
        Routes.PaymentSummary -> Pair(Icons.Default.Payment, MaterialTheme.colorScheme.secondary)
        else -> Pair(Icons.Default.Analytics, MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun TitleGridCard(
    onNavigate: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
//                            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.select_report),
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(),
//                                .padding(horizontal = 8.dp, vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                    val horizontalSpacing = 8.dp
                    val verticalSpacing = 8.dp
                    val columns = 5
                    val rows = (reports.size + columns - 1) / columns
                    val rowHeight = 88.dp

                    val gridHeight: Dp = remember(reports.size) {
                        rowHeight * rows + verticalSpacing * (rows - 1)
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(gridHeight),
                        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    ) {
                        items(reports) { item ->
                            val (icon, color) = iconForReport(item)
                            ReportButton(
                                label = stringResource(item.titleRes),
                                onClick = { onNavigate(item.route) },
                                icon = icon,
                                iconBg = color,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }


@Composable
fun DailyCollectionHeader(
    date: String,
    dateState: String?,
    onDateSelected: (String) -> Unit,
    showPicker: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Daily Collections",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = "Pick date",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = date.ifEmpty { dateState ?: "-" },
            modifier = Modifier
                .clickable { showPicker.value = true }
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        if (showPicker.value) {
            ModalDatePicker(
                onDateSelected = { date ->
//                    viewModel.setFromDate(date)
                    onDateSelected(date)
                    showPicker.value = false
                },
                onDismiss = { showPicker.value = false }
            )
        }
    }
}

@Composable
fun TableHeader() {
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
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Txn. Amount",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun DailyCollectionRow(exec: ExecutiveCollection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = exec.executiveName, style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            text = "₹${exec.totalCollected}",
            modifier = Modifier.wrapContentWidth(Alignment.End),
            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Green,
            textAlign = TextAlign.End
        )
    }
}


