package com.pepsigo.admin.screens.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> SalesReportStatus(
    item: T,
    status: (T) -> String
) {
    val statusText = status(item)
    val (bgColor, textColor) = when (statusText.lowercase()) {

        "sale" -> {MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.tertiary}

        "return" -> {MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary}

        "cancelled" -> {MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error}

        else -> {MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant}
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SalesReportStatusPreview() {
    MaterialTheme {
        SalesReportStatus(item = "Sale", status = { it })
    }
}


