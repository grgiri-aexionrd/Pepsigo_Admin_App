package com.pepsigo.admin.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun MakePaymentReviewScreen(
    state: MakePaymentUiState,
    onConfirm: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.payment_summary),
                icon = Icons.Default.Check,
                desc = stringResource(R.string.payment_summary),
                onBackClick = onEdit
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Transaction Reference Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.transaction_reference),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        state.saleId?.let {
                            SummaryRow(
                                label = stringResource(R.string.sale_id),
                                value = it
                            )
                        }

                        state.purchaseId?.let {
                            SummaryRow(
                                label = stringResource(R.string.purchase_id),
                                value = it
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (state.partyType == PartyType.CUSTOMER)
                                stringResource(R.string.customer_details)
                            else
                                stringResource(R.string.vendor_details),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        state.selectedCustomer?.let { customer ->
                            SummaryRow(
                                label = stringResource(R.string.customer_id),
                                value = customer.id.toString()
                            )
                            SummaryRow(
                                label = stringResource(R.string.name),
                                value = customer.name
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.payment_details),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        state.totalAmount?.let {
                            SummaryRow(
                                label = stringResource(R.string.total_amount),
                                value = "₹$it"
                            )
                        }

                        SummaryRow(
                            label = stringResource(R.string.payment_amount),
                            value = "₹${state.paymentAmount}"
                        )

                        SummaryRow(
                            label = stringResource(R.string.payment_method),
                            value = state.paymentMethod
                        )

                        SummaryRow(
                            label = stringResource(R.string.transaction_type),
                            value = state.transactionType.replaceFirstChar { it.uppercase() }
                        )

                        if (state.refNumber.isNotBlank()) {
                            SummaryRow(
                                label = stringResource(R.string.reference_number),
                                value = state.refNumber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Denomination Details Card
                val hasCashDenomination = state.cashDenomination.let {
                    it.denom2000 > 0 || it.denom500 > 0 || it.denom200 > 0 ||
                            it.denom100 > 0 || it.denom50 > 0 || it.denom20 > 0 ||
                            it.denom10 > 0 || it.denom5 > 0 || it.denom2 > 0 || it.denom1 > 0
                }

                val hasDigitalDenomination = state.digitalDenomination.let {
                    it.card > 0 || it.upi > 0 || it.netBanking > 0 || it.cheque > 0 || it.credit > 0
                }

                if (hasCashDenomination || hasDigitalDenomination) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.denomination_details),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            // Cash denominations
                            if (hasCashDenomination) {
                                Text(
                                    text = stringResource(R.string.cash),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val cash = state.cashDenomination
                                if (cash.denom2000 > 0) {
                                    DenominationSummaryRow("₹2000", cash.denom2000, 2000)
                                }
                                if (cash.denom500 > 0) {
                                    DenominationSummaryRow("₹500", cash.denom500, 500)
                                }
                                if (cash.denom200 > 0) {
                                    DenominationSummaryRow("₹200", cash.denom200, 200)
                                }
                                if (cash.denom100 > 0) {
                                    DenominationSummaryRow("₹100", cash.denom100, 100)
                                }
                                if (cash.denom50 > 0) {
                                    DenominationSummaryRow("₹50", cash.denom50, 50)
                                }
                                if (cash.denom20 > 0) {
                                    DenominationSummaryRow("₹20", cash.denom20, 20)
                                }
                                if (cash.denom10 > 0) {
                                    DenominationSummaryRow("₹10", cash.denom10, 10)
                                }
                                if (cash.denom5 > 0) {
                                    DenominationSummaryRow("₹5", cash.denom5, 5)
                                }
                                if (cash.denom2 > 0) {
                                    DenominationSummaryRow("₹2", cash.denom2, 2)
                                }
                                if (cash.denom1 > 0) {
                                    DenominationSummaryRow("₹1", cash.denom1, 1)
                                }

                                // Cash total
                                val cashTotal = (cash.denom2000 * 2000) + (cash.denom500 * 500) +
                                        (cash.denom200 * 200) + (cash.denom100 * 100) +
                                        (cash.denom50 * 50) + (cash.denom20 * 20) +
                                        (cash.denom10 * 10) + (cash.denom5 * 5) +
                                        (cash.denom2 * 2) + (cash.denom1 * 1)

                                if (cashTotal > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.cash_total),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "₹$cashTotal",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Digital denominations
                            if (hasDigitalDenomination) {
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = stringResource(R.string.other_payments),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val digital = state.digitalDenomination
                                if (digital.card > 0) {
                                    SummaryRow(
                                        label = stringResource(R.string.card),
                                        value = "₹${digital.card}"
                                    )
                                }
                                if (digital.upi > 0) {
                                    SummaryRow(
                                        label = stringResource(R.string.upi),
                                        value = "₹${digital.upi}"
                                    )
                                }
                                if (digital.netBanking > 0) {
                                    SummaryRow(
                                        label = stringResource(R.string.net_banking),
                                        value = "₹${digital.netBanking}"
                                    )
                                }
                                if (digital.cheque > 0) {
                                    SummaryRow(
                                        label = stringResource(R.string.cheque),
                                        value = "₹${digital.cheque}"
                                    )
                                }
                                if (digital.credit > 0) {
                                    SummaryRow(
                                        label = stringResource(R.string.credit),
                                        value = "₹${digital.credit}"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Error message
                state.error?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.saving))
                        } else {
                            Text(stringResource(R.string.save_payment))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DenominationSummaryRow(
    denomination: String,
    count: Int,
    value: Int
) {
    val total = count * value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$denomination × $count",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "= ₹$total",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MakePaymentReviewScreenPreview() {
    MakePaymentReviewScreen(
        state = MakePaymentUiState(
            saleId = "123",
            totalAmount = "1000",
            selectedCustomer = com.pepsigo.admin.screens.reports.DropDownList(
                id = 1,
                name = "John Doe"
            ),
            paymentAmount = "1000",
            paymentMethod = "Cash on Delivery",
            transactionType = "debit",
            refNumber = "REF123",
            cashDenomination = CashDenomination(
                denom500 = 1,
                denom100 = 5
            ),
            digitalDenomination = DigitalDenomination(),
            isLoading = false
        ),
        onConfirm = {},
        onEdit = {}
    )
}