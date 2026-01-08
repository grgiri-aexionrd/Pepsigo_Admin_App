package com.pepsigo.admin.screens.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWiseSalesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(R.string.item_wise_sales),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                desc = stringResource(R.string.item_wise_sales),
                onBackClick = { onNavigateBack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // empty screen content
        }
    }
}
