package com.pepsigo.admin.screens.printInvoice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.utils.createInvoicePdf
import com.pepsigo.admin.utils.printInvoice
import com.pepsigo.admin.utils.printInvoicePdf
import com.pepsigo.admin.utils.shareInvoicePrint

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintInvoiceScreen(
    viewModel: PrintInvoiceViewModel,
    onNavigateBack: () -> Unit,
    onPrint: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current


    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = "Print Invoice",
                icon = Icons.Default.Print,
                desc = "Print Invoice",
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.loading),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                state.error != null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = state.error ?: "Error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { viewModel.loadInvoice() }) {
                                Text(text = stringResource(R.string.retry))
                            }
                        }
                    }
                }

                else -> {
                    // Success - show invoice
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Invoice preview card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = state.invoiceText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Print button
                        Button(
                            onClick = {
//                                shareInvoicePrint(context = context, state.invoiceText)
//                                printInvoice(context, state.invoiceText)
                                val pdf = createInvoicePdf(context, state.invoiceText)
                                printInvoicePdf(context, pdf)
                                      },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = stringResource(R.string.print_invoice))
                        }
                    }
                }
            }
        }
    }
}
