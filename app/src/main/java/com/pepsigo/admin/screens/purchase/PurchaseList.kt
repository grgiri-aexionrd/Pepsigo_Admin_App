package com.pepsigo.admin.screens.purchase

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import com.pepsigo.admin.model.PurchaseUiModel

@Composable
fun PurchaseList(
    purchases: LazyPagingItems<PurchaseUiModel>,
    modifier: Modifier = Modifier,
    onItemClick: (PurchaseUiModel) -> Unit
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
                    text = "Purchase List",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        items(count = purchases.itemCount) { purchase ->
            val item = purchases[purchase]
            if (item != null) {
                PurchaseCard(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // Loading states
        when (val state = purchases.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingMoreItem()
                }
            }
            is LoadState.Error -> {
                item {
                    LoadMoreRetry(
                        message = state.error.message ?: "Something went wrong",
                        onRetry = { purchases.retry() }
                    )
                }
            }
            else -> {}
        }

        // Handle initial empty state
        if (purchases.loadState.refresh is LoadState.NotLoading && purchases.itemCount == 0) {
            item { EmptyView() }
        }
    }




}

@Composable
fun PurchaseCard(item: PurchaseUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
//            .clickable { expanded = !expanded },
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Invoice#: ${item.invoiceNumber}", fontWeight = FontWeight.SemiBold)
                Text(item.purchaseDate, color = Color.Gray, fontSize = 13.sp)
            }

            Spacer(Modifier.height(6.dp))

            // Vendor + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vendor: ${item.vendor.businessName}")
                PurchaseStatusChip(status = item.invoiceStatus)
            }

            Divider(Modifier.padding(vertical = 10.dp))

            // Amount Summary
            Column {
                InfoRow(label = "Subtotal", value = item.subTotal)
                InfoRow(label = "Discount (BT)", value = item.discountBt)
                InfoRow(label = "Discount (AT)", value = item.discountAt)
                InfoRow(label = "Tax Amount", value = item.taxAmount)
            }

            Divider(Modifier.padding(vertical = 10.dp))

            // Total Payable
            Text(
                "TOTAL PAYABLE: ${item.totalAmount}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PurchaseStatusChip(status: String) {
    val chipColor = when (status.lowercase()) {
        "purchase" -> Color(0xFF4CAF50) // Green
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
            fontWeight = FontWeight.SemiBold
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
        Text("No purchases found")
    }
}