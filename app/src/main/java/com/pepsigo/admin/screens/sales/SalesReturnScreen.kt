package com.pepsigo.admin.screens.sales

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepsigo.admin.model.InventoryUi
import com.pepsigo.admin.model.SaleBatchUi
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesItemsDetailUi

@Composable
fun SalesReturnScreen(
    returnItem: SalesDetailUi,
    onCheckedChange: (SalesItemsDetailUi, Boolean) -> Unit,
    onQuantityChange: (SalesItemsDetailUi, Int) -> Unit,
    currentSelection: Map<Int, SalesReturnItemList>,
    onReturnCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Select Items to Return",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(returnItem.salesItems) { item ->
                val selection = currentSelection[item.inventory.invId]
                val checked = selection != null
                val quantity = selection?.quantity?.toInt() ?: 1

                SalesReturnItemCard(
                    item = item,
                    checked = checked,
                    quantity = quantity,
                    onCheckedChange = { onCheckedChange(item, it) },
                    onQuantityChange = { onQuantityChange(item, it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Add space at bottom for buttons
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        SalesCancelReturnButtons(
            onReturnCancel = onReturnCancel,
            onReturn = onReturn,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
fun SalesReturnItemCard(
    item: SalesItemsDetailUi,
    checked: Boolean,
    quantity: Int,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Row {
                Column {
                    Text(
                        item.inventory.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Sold Quantity",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Unit Price",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column {
                    Text("", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${item.itemQuantity} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        item.salePrice,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (checked) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                SalesQuantityStepper(
                    quantity = quantity,
                    maxQuantity = item.itemQuantity.toIntOrNull() ?: 1,
                    onQuantityChange = onQuantityChange
                )
            }
        }
    }
}

@Composable
fun SalesQuantityStepper(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.width(40.dp))
        Text(
            text = "Return Quantity",
            style = MaterialTheme.typography.bodyMedium
        )
        // MINUS BUTTON
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            enabled = quantity > 1,
            modifier = Modifier
                .background(
                    color = Color(0xFFF1F1F1),
                    shape = CircleShape
                )
        ) {
            Text("–", fontSize = 20.sp)
        }
        // CURRENT VALUE
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium
        )
        // PLUS BUTTON
        IconButton(
            onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) },
            enabled = quantity < maxQuantity,
            modifier = Modifier
                .background(
                    color = Color(0xFFF1F1F1),
                    shape = CircleShape
                )
        ) {
            Text("+", fontSize = 20.sp)
        }
    }
}

@Composable
fun SalesCancelReturnButtons(
    onReturnCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(onClick = onReturnCancel) {
            Text("Cancel")
        }
        Button(onClick = onReturn) {
            Text("Return")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesReturnScreenPreview() {
    val sampleItem = SalesItemsDetailUi(
        id = 1,
        saleId = 1,
        inventoryId = 1,
        batchNumber = 1,
        itemQuantity = "10",
        unit = "pcs",
        gstPercent = "18%",
        costPrice = "80.00",
        salePrice = "100.00",
        retailPrice = "110.00",
        totalAmount = "1000.00",
        inventory = InventoryUi(
            invId = 1,
            name = "Product A",
            openingQuantity = "100",
            unit = "pcs",
            gstPercent = "18%"
        ),
        batch = SaleBatchUi(
            id = 1,
            invoiceNumber = "PUR-001",
            vendorId = 1,
            purchaseDate = "2026-01-15",
            subTotal = "800.00",
            discountBt = "0.00",
            taxAmount = "144.00",
            discountAt = "0.00",
            totalAmount = "944.00",
            invoiceStatus = "Purchase"
        )
    )

    SalesReturnItemCard(
        item = sampleItem,
        checked = true,
        quantity = 2,
        onCheckedChange = {},
        onQuantityChange = {}
    )
}
