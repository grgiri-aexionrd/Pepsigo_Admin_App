package com.pepsigo.admin.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepsigo.admin.R
import com.pepsigo.admin.domainLayer.ExpiryStatus
import com.pepsigo.admin.domainLayer.StockStatus
import com.pepsigo.admin.model.BatchStockDetail
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun BatchSummaryScreen(
    viewModel: BatchSummaryViewModel,
    onNavigateBack: () -> Unit
){
    val batchSummary by viewModel.batchStock.collectAsState()
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val visibleBatchData by remember(batchSummary.batchStock, batchSummary.filter) {
        derivedStateOf {
            when (batchSummary.filter) {
                BatchFilter.ALL -> batchSummary.batchStock
                BatchFilter.EXPIRING_SOON -> batchSummary.batchStock.filter { it.expiryStatus == ExpiryStatus.EXPIRING_SOON }
                BatchFilter.EXPIRED -> batchSummary.batchStock.filter { it.expiryStatus == ExpiryStatus.EXPIRED }
                BatchFilter.LOW_STOCK -> batchSummary.batchStock.filter { it.stockStatus == StockStatus.LOW_STOCK }
                BatchFilter.NO_STOCK -> batchSummary.batchStock.filter { it.stockStatus == StockStatus.NO_STOCK }
            }
        }
    }
    LaunchedEffect(batchSummary.snackbarMessage) {
        batchSummary.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = "Batch Summary",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = "Batch Summary",
                onBackClick = { onNavigateBack() }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState){data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (batchSummary.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (batchSummary.isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {innerPadding ->
        PullToRefreshBox(
            isRefreshing = batchSummary.isRefreshing,
            onRefresh = { viewModel.refresh()  },
            state = refreshState,
            modifier = Modifier.padding(innerPadding),
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // product dropdown
                stickyHeader {
                    ProductDropDown(
                        state = batchSummary,
                        onSelected = { selected ->
                            viewModel.updateSelectedInventory(selected)
                        },
                        onGetDetails = { selectedId ->
                            viewModel.fetchBatchStockDetails(selectedId)
                        },
                        onFilterSelect = { filter ->
                            viewModel.updateFilter(filter)
                        }
                    )


                }
                if(batchSummary.hasFetchedDetails) {
                    // BatchSummaryCard
//                    item {
//                        BatchSummaryCard(
//                            count = batchSummary.count,
//                            qty = batchSummary.totalAvailableQuantity,
//                            inventorySelected = batchSummary.selectedInventory
//                        )
//                    }

                    // Batch Stock Detail Card
                    if (visibleBatchData.isEmpty()) {
                        item {
                            EmptyBatchCard()
                        }
                    }else {
                        items(
                            visibleBatchData,
                            key = { "${it.batchId}_${it.itemName}_${it.unit}"  }
                        ) { batch ->
                            BatchStockDetailCard(
                                batch = batch
                            )
                        }
                    }

                }

            }

        }

    }

}



@Composable
fun ProductDropDown(
    state: BatchSummaryUiState,
    onSelected: (DropDownList?) -> Unit,
    onGetDetails: (Int?) -> Unit,
    onFilterSelect: (BatchFilter) -> Unit
){
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.product_dropdown),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            DropDown(
                dropDown = state.inventory,
                error = state.selectedInventoryError,
                label = stringResource(R.string.select_product),
                selected = state.selectedInventory,
                labelExtractor = {it.name},
                onSelected = { selected ->
                    onSelected(selected)
                }
            )
            Button (
                onClick = {
                    val selectedId = state.selectedInventory?.id
                    onGetDetails(selectedId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !state.isLoading
            ){
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Get Details", color = Color.White)
                }
            }
            FilterRow(
                selectedFilter = state.filter,
                onFilterSelect = {  filter ->
                    onFilterSelect(filter)
                }
            )

        }
    }
}

@Composable
fun BatchSummaryCard(
    count: Int,
    qty: Int,
    inventorySelected: DropDownList?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // minimal elevation for separation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
//            val name: String =
//                if (inventorySelected?.id == -1) "All Items" else inventorySelected?.name!!
//            Text(
//                text = name,
//                style = MaterialTheme.typography.titleLarge
//            )
            Column {
                Text(
                    text = "Count",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = count.toString())
            }
            Column {
                Text(
                    text = " Qty",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = qty.toString())
            }

        }
    }

}

enum class BatchFilter(val label:String){
    ALL("All"),
    EXPIRING_SOON("Expiring Soon"),
    EXPIRED("Expired"),
    LOW_STOCK("Low Stock"),
    NO_STOCK("No Stock")
}

@Composable
fun FilterRow(
    selectedFilter: BatchFilter ,
    onFilterSelect: (BatchFilter) -> Unit
){
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BatchFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelect(filter) },
                    label = { Text(filter.label) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

}

@Composable
fun EmptyBatchCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // minimal elevation for separation
    ){
        Text(
            stringResource(R.string.no_batch_data),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BatchStockDetailCard(
    batch: BatchStockDetail
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // minimal elevation for separation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = batch.itemName,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(horizontalArrangement = Arrangement.End) {
                    BatchStatusChip(
                        status = batch.stockStatus,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BatchStatusChip(
                        status = batch.expiryStatus,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text(text = "Batch #${batch.batchId}")
                    Text(text = "Available: ${batch.availableQuantity} ${batch.unit}")
                }
                Text(text = "Expiry: ${batch.expiryDate}")

            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text("Cost Price")
                    Text("₹${batch.costPrice}")
                }
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text("Sale Price")
                    Text("₹${batch.salePrice}")
                }

            }


        }

    }

}

@Composable
fun BatchStatusChip(
    status: Any,
    modifier: Modifier = Modifier
){
    val (label, color) = when (status) {

        is StockStatus -> when (status) {
            StockStatus.NO_STOCK -> "No Stock" to Color(0xFFF44336)
            StockStatus.LOW_STOCK -> "Low Stock" to Color(0xFFFFC107)
            StockStatus.IN_STOCK -> "In Stock" to Color(0xFF4CAF50)
            else -> "Unknown" to Color.Gray
        }

        is ExpiryStatus -> when (status) {
            ExpiryStatus.NO_EXPIRY -> "No Expiry" to Color(0xFF9E9E9E)
            ExpiryStatus.EXPIRED -> "Expired" to Color(0xFFF44336)
            ExpiryStatus.EXPIRING_SOON -> "Expiring Soon" to Color(0xFFFF9800)
            ExpiryStatus.NORMAL -> "Normal" to Color(0xFF4CAF50)
            else -> "Unknown" to Color.Gray
        }

        else -> "Unknown" to Color.Gray
    }

    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}

