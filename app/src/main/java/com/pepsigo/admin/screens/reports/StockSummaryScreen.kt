package com.pepsigo.admin.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.StockSummaryData
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun StockSummaryScreen(
    viewModel: StockSummaryViewModel,
    onNavigateBack: () -> Unit
) {
    val stockSummary by viewModel.stockSummary.collectAsState()
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    val visibleItems by remember(stockSummary.stockSummary, stockSummary.selectedFilter) {
        derivedStateOf {
            stockSummary.selectedFilter?.let { product ->
                stockSummary.stockSummary.filter { it.itemName.startsWith(product, ignoreCase = true) }
            } ?: stockSummary.stockSummary
        }
    }

    LaunchedEffect(stockSummary.snackbarMessage) {
        stockSummary.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = "Stock Summary",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = "Stock Summary",
                onBackClick = { onNavigateBack() }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState){data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (stockSummary.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (stockSummary.isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )

            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = stockSummary.isRefreshing,
            onRefresh = { viewModel.refresh() },
            state = refreshState,
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Page title
                item{
                    Text(
                        text = "Stock Summary",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    ProductFilterChips(
                        filterStock = stockSummary.filterStock,
                        selectedFilter = stockSummary.selectedFilter,
                        onFilterSelect = { selected ->
                            viewModel.onProductSelected(selected)
                        }
                    )
                }

                if (stockSummary.isError || stockSummary.stockSummary.isEmpty()){
                    item{
                        EmptyStockCard(stockSummary.emptyCardMessage)
                    }
                }
                else {

                    items(visibleItems) { item ->
                        StockSummaryReportCard(item)
                    }
                }

            }
        }
    }

}

@Composable
fun ProductFilterChips(
    filterStock: List<String>,
    selectedFilter: String?,
    onFilterSelect: (String?) -> Unit
){
    LazyRow(
//        modifier = Modifier.padding(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelect(null) },
                label = { Text("All") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        items(filterStock) { product ->
            FilterChip(
                selected = selectedFilter == product,
                onClick = { onFilterSelect(product) },
                label = { Text(product) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }

}

@Composable
fun StockSummaryReportCard(
    item : StockSummaryData
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // minimal elevation for separation
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleMedium
                )
                BatchStatusChip(item.stockStatus)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(modifier = Modifier
                    .wrapContentSize()
                ){
                    Text("Unit")
                    Text(item.unit)
                }
                Column(modifier = Modifier
                    .wrapContentSize()
                ){
                    Text("GST")
                    Text("${item.gstPercent}%")
                }
                Column(modifier = Modifier
                    .wrapContentSize()
                ){
                    Text("Available")
                    Text("${item.availableQuantity}")
                }
            }
        }

    }

}

@Composable
fun EmptyStockCard(
    message : String? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // minimal elevation for separation
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                message ?: "No Stock Data Available",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            if(message != null) {
                Text(
                    "Pull to refresh",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}