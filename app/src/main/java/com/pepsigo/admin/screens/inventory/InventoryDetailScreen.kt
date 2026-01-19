package com.pepsigo.admin.screens.inventory

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.BatchUi
import com.pepsigo.admin.model.InventoryItemDetailUi
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.OfferDetailUi
import com.pepsigo.admin.model.StockSummaryUi
import com.pepsigo.admin.utils.formatExpiryDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InventoryDetailScreen(
    inventoryDetail: InventoryItemDetailUi?,
    isError: Boolean,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
//            .padding(padding)
            .fillMaxSize()
    ){
        item {
            Text("Item Details",style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            ItemBasicInfoCard(inventoryDetail?.itemDetail)
            Spacer(Modifier.height(8.dp))
        }

        if (!isError) {
//            if (inventoryDetail?.offer == "Offered") {
            item { Text("Offers: ${inventoryDetail?.offer}")
                Spacer(Modifier.height(8.dp))
            }
                item { OfferDetailsCard(inventoryDetail?.offer,
                    inventoryDetail?.offerDetail)
                    Spacer(Modifier.height(8.dp))
                }
//            }

            item {
                Text("Stock Summary")
                Spacer(Modifier.height(8.dp))
                StockSummaryCard(inventoryDetail?.stockSummary)
                Spacer(Modifier.height(8.dp))
            }
            item {
                Text("Batches (${inventoryDetail?.batches?.size})")
                Spacer(Modifier.height(8.dp))
            }
            items(inventoryDetail?.batches ?: emptyList()) { batch ->
                BatchCard(batch)
                Spacer(Modifier.height(8.dp))
            }
        }else{
            item {
                Text("Error")
            }
        }
    }

}

@Composable
fun ItemBasicInfoCard(
    itemDetail: InventoryListUi?
) {
    // Implementation of the basic info card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row {
                Text(text = itemDetail?.name ?: "Item Name",style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                StatusChip(isEnabled = itemDetail?.enabled)
            }
            Row{
                Text("Unit", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(56.dp))
                Text("GST",style = MaterialTheme.typography.bodyMedium)
            }

            Row{
                Text(itemDetail?.unit ?: "", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(56.dp))
                Text(itemDetail?.gstPercent.toString(), style = MaterialTheme.typography.bodyLarge)
            }
        }

    }

}

@Composable
fun StatusChip(
    isEnabled: Boolean?
) {
    // Implementation of the status chip
    val bg = if (isEnabled == true) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
    val status = if (isEnabled == true) "Enabled" else "Disabled"

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ){
        Text(text = status, style = MaterialTheme.typography.labelSmall)
    }


}

@Composable
fun OfferDetailsCard(
    offer: String? = "--",
    offerDetail: OfferDetailUi?
) {
    // Implementation of the offer details card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Type: $offer")
                Text("Sale Price: ₹${offerDetail?.salePrice?:0.00}")
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Quantity: ${offerDetail?.quantity?:"--"}")
                Text("Customer ID: ${offerDetail?.customerId?:"--"}")
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockSummaryCard(
    stockSummary: StockSummaryUi?
) {
    // Implementation of the stock summary card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween){
                    Text("Total Available: ${stockSummary?.totalAvailable}")
                    Text("Active Batches: ${stockSummary?.batchesCount}")
            }
//            Text("Nearest Expiry: ${formatExpiryDate(stockSummary?.nearestExpiry)}")
            Text("Nearest Expiry: ${stockSummary?.nearestExpiry}")
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BatchCard(
    batch: BatchUi
){
    // Implementation of the batch card
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Batch #${batch.id}", style = MaterialTheme.typography.titleMedium)
//                Text("Expiry: ${formatExpiryDate(batch.expiryDate)}")
                Text("Expiry: ${batch.expiryDate}")
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Show Batch Details",
                    modifier = Modifier
                        .rotate(rotation)
                        .clickable { expanded = !expanded },
                )
            }
            Text("Available: ${batch.availableQuantity} units")

            AnimatedVisibility(expanded){
                Column{
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                            Text("Purchased")
                            Text("Sold")
                            Text("Available")

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(" ${batch.purchasedQuantity}")
                        Text(" ${batch.soldQuantity}")
                        Text(" ${batch.availableQuantity}")

                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("Cost Price")
                        Text("Sale Price")
                        Text("Retail Price")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(" ₹${batch.costPrice}")
                        Text(" ₹${batch.salePrice}")
                        Text(" ₹${batch.retailPrice}")
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("Purchase Date")
                        Text("Expired")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(" ${formatExpiryDate(batch.purchasedDate)}")
                        Text(" ${if (batch.expired) "Yes" else "No"}")

                    }
                }

            }
        }


    }
}