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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun ReturnSummaryScreen(
    purchaseId: Int,
    invoiceNumber : String,
    returnSummaryItem: List<ReturnItemList>,
    onReturnCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(modifier = Modifier.fillMaxSize())
    {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ){
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    " Return Summary ",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item{
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "InvoiceNumber",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            invoiceNumber,
                            textAlign = TextAlign.Center
                        )
                    }
//                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(returnSummaryItem){item ->
                Spacer(modifier = Modifier.height(8.dp))
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
                        modifier = Modifier.padding(16.dp)
                    ){
                        Text( item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text("Return Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                    }

                }


            }



        }


        ConfirmReturnButtons(
            onCancel = onReturnCancel ,
            onReturn = onReturn,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )
    }

}

@Composable
fun ConfirmReturnButtons(
    onCancel: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){

        OutlinedButton (onClick =  onCancel ){
            Text("Cancel")
        }
        Button(onClick = onReturn ){
            Text("Confirm Return")
        }
    }

}