package com.pepsigo.admin.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.utils.toAppError

@Composable
fun SalesList(
    sales: LazyPagingItems<SalesUiModel>,
    modifier: Modifier = Modifier,
    onItemClick: (SalesUiModel) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(vertical = 8.dp)
    ){
        stickyHeader {
            Row (modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Sales List",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        items(count = sales.itemCount) { index ->
            val item = sales[index]
            if (item != null) {
                SaleCard(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // Loading states
        when (val state = sales.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingMoreItem()
                }
            }
            is LoadState.Error -> {
                val appError = state.error.toAppError()
                item {
                    LoadMoreRetry(
                        message = appError.userFriendlyMessage,
                        onRetry = { sales.retry() }
                    )
                }
            }
            else -> {}
        }

        // Handle initial empty state
        if (sales.loadState.refresh is LoadState.NotLoading && sales.itemCount == 0) {
            item { EmptyView() }
        }
    }
}

@Composable
fun SaleCard(item: SalesUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Top row: Invoice + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                ) {
                    Text(item.customer.businessName, fontWeight = FontWeight.SemiBold)
                    Text(item.invoiceNumber,color = Color.Gray,style = MaterialTheme.typography.labelLarge )

                }
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    SalesStatusChip(status = item.invoiceStatus)
                    Text(item.saleDate, color = Color.Gray, fontSize = 13.sp)
                }

            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                SalesInfoRow(label = "Subtotal", value = item.subTotal)
                SalesInfoRow(label = "Tax Amount", value = item.taxAmount)
                SalesInfoRow(label = "Total", value = item.totalAmount)
            }

            // Amount Summary
//            Column {
//                InfoRow(label = "Subtotal", value = item.subTotal)
//                InfoRow(label = "Discount (BT)", value = item.discountBt)
//                InfoRow(label = "Discount (AT)", value = item.discountAt)
//                InfoRow(label = "Tax Amount", value = item.taxAmount)
//            }

//            HorizontalDivider()
//
//            // Total Payable
//            Text(
//                "TOTAL PAYABLE: ${item.totalAmount}",
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp,
//                color = MaterialTheme.colorScheme.primary
//            )
        }
    }
}

@Composable
fun SalesInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SalesStatusChip(status: String) {
    val chipColor = when (status.lowercase()) {
        "sale" -> Color(0xFF4CAF50) // Green
        "return" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .background(chipColor.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = chipColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun LoadMoreRetry(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.Red, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun LoadingMoreItem() {
    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("No sales found")
    }
}