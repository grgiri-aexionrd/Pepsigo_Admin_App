package com.pepsigo.admin.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.screens.commonComponents.ReportCard
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@Composable
fun TestReportScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = "Sale Report",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = "Sale Report",
                onBackClick = { onNavigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Filters Card (dates + chips)
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Use the new InlineDateRangePicker here
//                    InlineDateRangePicker(
//                        label = "Custom",
//                        fromDate = "01/12/2025",
//                        toDate = "31/01/2026",
//                        onFromDateSelected = {},
//                        onToDateSelected = {}
//                    )

//                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter chips mock
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipMock(text = "Txns Type - Sale & Cr. Note")
                        ChipMock(text = "Party - All Party")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary cards row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(modifier = Modifier.weight(1f), title = "No of Txns", value = "1")
                SummaryCard(modifier = Modifier.weight(1f), title = "Total Sale", value = "₹ 1,000.00")
                SummaryCard(modifier = Modifier.weight(1f), title = "Balance Due", value = "₹ 500.00", valueColor = Color(0xFF24A148))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction card
            ReportCard(
                item = sampleTransaction,
                title = { it.title },
                subtitle = { it.subtitle },
                status = { it.status },
                dateValue = { it.date },
                amountValue = { it.amount },
                bottomItems = { listOf("Amount" to "₹ 1,000.00", "Balance" to "₹ 500.00") }
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun ChipMock(text: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F8)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = value, color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private data class SampleTransaction(
    val title: String, val subtitle: String, val status: String, val date: String, val amount: String
)

private val sampleTransaction = SampleTransaction(
    title = "Acme solution",
    subtitle = "SALE 1\n31 DEC, 25",
    status = "",
    date = "31 DEC, 25",
    amount = "₹ 1,000.00"
)


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun TestReportPreview() {
    TestReportScreen()
}
