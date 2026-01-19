package com.pepsigo.admin.screens.purchase

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.AmountSummaryUi
import com.pepsigo.admin.model.ItemsDetailUi
import com.pepsigo.admin.model.PurchaseDetailUi
import com.pepsigo.admin.model.PurchaseUi
import com.pepsigo.admin.model.User

@Composable
fun PurchaseDetails(
    purchase: PurchaseDetailUi,
    onCancelClick: (id:Int) -> Unit,
    onReturnClick:(purchase: PurchaseDetailUi ) -> Unit,
    modifier : Modifier,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = modifier
//            .padding(padding)
            .fillMaxSize()
    ){
        // Invoice Details
        item {
            Text(
                "Purchase Details",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            PurchaseBasicInfo(purchase.purchase)
            Spacer(Modifier.height(8.dp))
        }

        // Vendor Details
        item{
            Text(
                stringResource(R.string.vendor_details),
                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            VendorBasicInfo(purchase.vendor)
            Spacer(Modifier.height(8.dp))

        }

        // Amount Details
        item{
            Text(
                stringResource(R.string.amount_details),
                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            AmountDetails(purchase.amountSummary)
            Spacer(Modifier.height(8.dp))
        }

        // button to cancel or return items
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("Sales: " + if (purchase.hasSales) "Yes" else "No")

                Spacer(modifier = Modifier.weight(1f)) // pushes buttons to right

                when(purchase.purchase.invoiceStatus){
                    "Cancelled" -> { Text("Cancelled Items") }
                    "Return" -> { Text("Return Items") }
                    else -> {}
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    if(purchase.purchase.invoiceStatus != "Cancelled") {
                        if(!purchase.hasSales) {
                            OutlinedButton(
                                onClick = { onCancelClick(purchase.purchase.purchaseId) },
                                enabled = !purchase.hasSales,
                                border = BorderStroke(
                                    2.dp,
                                    if (!purchase.hasSales) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.secondary
                                ),
//                        modifier = Modifier.wrapContentSize()
                                modifier = Modifier
                                    .height(56.dp)
                                    .width(112.dp)
                            ) {
                                if (isLoading) CircularProgressIndicator() else Text("Cancel Purchase")

                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    if(purchase.purchase.invoiceStatus == "Purchase") {

                        Button(
                            onClick = { onReturnClick(purchase) },
//                        modifier = Modifier.wrapContentSize()
                            modifier = Modifier
                                .height(56.dp)
                                .width(112.dp)
                        ) {
                            if (isLoading) CircularProgressIndicator() else Text("Return Item")
                        }
                    }
                }
            }
        }

        // Purchased item details
        items(purchase.purchasedItems){ item ->
            PurchasedItem(item)
            Spacer(Modifier.height(8.dp))
        }

    }


}

@Composable
fun PurchaseBasicInfo(
    purchase: PurchaseUi
){
    // Implementation of the basic info
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text("Invoice#: ", fontWeight = FontWeight.SemiBold)
                    Text(purchase.invoiceNumber)
                }
                Column(){
                    Text("Purchase Date", fontWeight = FontWeight.SemiBold)
                    Text(purchase.purchaseDate, color = Color.Gray )
//                    fontSize = 13.sp
                }

                PurchaseStatusChip(status = purchase.invoiceStatus)

            }
    }
}

@Composable
fun VendorBasicInfo(
    vendor: User
){
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text("Name", fontWeight = FontWeight.SemiBold)
                Text(vendor.name, )
                Text(vendor.businessName)
            }
            Column(
                modifier = Modifier.widthIn(max = 200.dp)
            ) {
                Text("Contact", fontWeight = FontWeight.SemiBold)
                Text(vendor.mobile )
                Text(vendor.email , maxLines = 2)
            }
        }
    }
}

@Composable
fun AmountDetails(
    amountSummary: AmountSummaryUi
){
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
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column() {
                Text("Sub Total", fontWeight = FontWeight.SemiBold)
                Text(amountSummary.subTotal)
                Text("Tax Amount", fontWeight = FontWeight.SemiBold)
                Text(amountSummary.taxAmount)

            }
            Column() {
                Text("Discount-BT", fontWeight = FontWeight.SemiBold)
                Text(amountSummary.discountBt)
                Text("Total Amount", fontWeight = FontWeight.SemiBold)
                Text(amountSummary.totalAmount)
            }
            Column(){
                Text("Discount-AT", fontWeight = FontWeight.SemiBold)
                Text(amountSummary.discountAt)
            }

        }

    }

}

@Composable
fun PurchasedItem(
    item : ItemsDetailUi
){
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text("Item Name", fontWeight = FontWeight.SemiBold)
                Text(item.inventory.name)
                Text("Cost Price", fontWeight = FontWeight.SemiBold)
                Text(item.costPrice)
                Text("Gst", fontWeight = FontWeight.SemiBold)
                Text(item.gstPercent)
                Text("Expiry Date", fontWeight = FontWeight.SemiBold)
                Text(item.expiryDate)

            }
            Column() {
                Text("Qty", fontWeight = FontWeight.SemiBold)
                Text(item.itemQuantity)
                Text("Sale Price", fontWeight = FontWeight.SemiBold)
                Text(item.salePrice)
                Text("Total Amount", fontWeight = FontWeight.SemiBold)
                Text(item.totalAmount)
            }
            Column() {
                Text("Unit", fontWeight = FontWeight.SemiBold)
                Text(item.unit)
                Text("Retail Price", fontWeight = FontWeight.SemiBold)
                Text(item.retailPrice)

            }
        }
    }

}