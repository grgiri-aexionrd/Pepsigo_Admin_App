package com.pepsigo.admin.screens.purchase

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepsigo.admin.model.ItemsDetailUi
import com.pepsigo.admin.model.PurchaseDetailUi

@Composable
fun ReturnScreen(
    returnItem: PurchaseDetailUi,
    returnItemList: List<ReturnItemList>,
    onCheckedChange: (ItemsDetailUi, Boolean) -> Unit,
    onQuantityChange: (ItemsDetailUi, Int) -> Unit,
    currentSelection: Map<Int, ReturnItemList>,
    onReturnCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
)
{
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

            items(returnItem.purchasedItems) { item ->
                val selection = currentSelection[item.inventory.invId]
                val checked = selection != null
                val quantity = selection?.quantity?.toInt() ?: 1

                ReturnItemCard(
                    item,
                    checked = checked,
                    quantity = quantity,
                    onCheckedChange = {onCheckedChange(item, it)},
                    onQuantityChange = { onQuantityChange(item, it)}
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        CancelReturnButtons(
            onReturnCancel = onReturnCancel,
            onReturn = onReturn,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )
    }


}

@Composable
fun ReturnItemCard(
    item: ItemsDetailUi,
    checked: Boolean,
    quantity: Int,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit

)
{
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
//            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange

            )
//            Spacer(modifier = Modifier.weight(1f))
            Row {
                Column() {
                    Text(
                        item.inventory.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("Purchased quantity")
                    Text("Expiry Date")
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column() {
                    Text("", style = MaterialTheme.typography.titleLarge,)
                    Text("${item.itemQuantity} ${item.inventory.unit}")
                    Text(item.expiryDate)
                }
            }
        }

        if (checked){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(8.dp),
//                horizontalArrangement = Arrangement.End
//            horizontalArrangement = Arrangement.SpaceBetween
            ){
                QuantityStepper(
                    quantity = quantity,
                    maxQuantity = item.itemQuantity.toInt(),
                    onQuantityChange = onQuantityChange
                )
            }

        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Spacer(modifier = Modifier.width(40.dp))
        Text(
            text = "Return Quantity",
//            style = MaterialTheme.typography.bodyMedium
        )
        // MINUS BUTTON
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            enabled = quantity > 1,
            modifier = Modifier
//                .size(16.dp)
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
//                .size(16.dp)
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
fun CancelReturnButtons(
    onReturnCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        OutlinedButton (onClick =  onReturnCancel ){
            Text("Cancel")
        }
        Button(onClick = onReturn ){
            Text("Return")
        }
    }

}