package com.pepsigo.admin.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.utils.toCurrency

@Composable
fun SalesReturnSuccessScreen(
    returnMessage: String,
    returnResponse: SalesReturnResponse,
    returnItemsTotalAmount: Double,
    isPaymentMade: Boolean = false,
    onPrintInvoice: () -> Unit,
    onMakePayment: (saleId: Int, customerId: Int, amount: Double) -> Unit,
    markPaymentMade: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Success Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success Message
        Text(
            text = returnMessage,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Return Details Card
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
                    text = "Return Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                // Return ID
                DetailRow(
                    label = "Return ID",
                    value = "#${returnResponse.id}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Invoice Number
                DetailRow(
                    label = "Invoice Number",
                    value = returnResponse.invoiceNumber
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Customer ID
                DetailRow(
                    label = "Customer ID",
                    value = "#${returnResponse.customerId}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sale Date
                DetailRow(
                    label = "Date",
                    value = returnResponse.saleDate
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Invoice Status
                DetailRow(
                    label = "Status",
                    value = returnResponse.invoiceStatus
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Total Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = returnItemsTotalAmount.toCurrency(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isPaymentMade) {
                // Print Invoice Button (shown after payment is made)
                OutlinedButton(
                    onClick = onPrintInvoice,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(R.string.print_invoice))
                }
            }
                // Make Payment Button (shown before payment is made)
                Button(
                    onClick = {
                        markPaymentMade()
                        onMakePayment(
                            returnResponse.id,
                            returnResponse.customerId,
                            returnItemsTotalAmount
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(R.string.make_payment))
                }

        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SalesReturnSuccessScreenPreview() {
    SalesReturnSuccessScreen(
        returnMessage = "Return Successful",
        returnResponse = SalesReturnResponse(
            id = 101,
            invoiceNumber = "RET-2026-001",
            routeAssignmentId = null,
            customerId = 10,
            deliveryBoyId = 13,
            saleDate = "2026-01-28",
            subTotal = 2500.0,
            discountBt = 0.0,
            taxAmount = 450.0,
            discountAt = 0.0,
            totalAmount = 2950.0,
            invoiceStatus = "Return",
            items = emptyList()
        ),
        returnItemsTotalAmount = 2950.0,
        onPrintInvoice = {},
        markPaymentMade = {},
        onMakePayment = { _, _, _ -> }
    )
}
