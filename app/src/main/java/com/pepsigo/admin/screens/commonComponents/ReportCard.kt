package com.pepsigo.admin.screens.commonComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R

@Composable
fun <T> ReportCard(
    item: T,
    title: (T) -> String,
    subtitle: (T) -> String,
    status: (T) -> String,
    dateLabel: String = stringResource(R.string.date),
    dateValue: (T) -> String,
    amountLabel: String = stringResource(R.string.total_amount),
    amountValue: (T) -> String,
    bottomItems: @Composable (T) -> List<Pair<String, String>>
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
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            // Invoice Number + Paid Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Column{
                    Text(title(item), style = MaterialTheme.typography.titleMedium)
                    Text(subtitle(item),style = MaterialTheme.typography.bodyMedium)
                }
                // Invoice Status
                SalesReportStatus(
                    item = item,
                    status = status // your field
                )

            }

            // Date + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column{
                    Text(dateLabel,style = MaterialTheme.typography.bodyMedium)
                    Text(dateValue(item))
                }
                Column(
                    horizontalAlignment = Alignment.End
                ){
                    Text(amountLabel,style = MaterialTheme.typography.bodyMedium)
                    Text(amountValue(item))
                }
            }

            HorizontalDivider()
            // Bottom Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                bottomItems(item).forEach { (label, value) ->
                    Column {
                        Text(label)
                        Text(value)
                    }
                }
            }
        }
    }
}

private data class SampleReport(
    val number: String,
    val subtitle: String,
    val status: String,
    val date: String,
    val amount: String,
    val bottomStats: List<Pair<String, String>>
)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ReportCardPreview() {
    val sample = SampleReport(
        number = "INV-00123",
        subtitle = "Customer: Acme Co.",
        status = "Paid",
        date = "2025-10-31",
        amount = "$1,234.56",
        bottomStats = listOf("Items" to "3", "Tax" to "$123.45")
    )

    ReportCard(
        item = sample,
        title = { it.number },
        subtitle = { it.subtitle },
        status = { it.status },
        dateValue = { it.date },
        amountValue = { it.amount },
        bottomItems = { it.bottomStats }
    )
}