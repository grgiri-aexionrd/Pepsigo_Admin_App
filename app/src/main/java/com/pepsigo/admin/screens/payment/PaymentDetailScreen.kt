package com.pepsigo.admin.screens.payment

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.DenominationRequest
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.model.UpdatePaymentRequest
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentDetailScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val cancelState by viewModel.cancelState.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back press - in edit mode go to detail view, otherwise navigate back
    BackHandler(enabled = true) {
        if (isEditMode) {
            isEditMode = false
        } else {
            onNavigateBack()
        }
    }

    // Clear detail state when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearDetailState()
            viewModel.clearUpdateState()
            viewModel.clearCancelState()
        }
    }

    // Handle update state changes
    LaunchedEffect(updateState) {
        when (updateState) {
            is PaymentUpdateState.Success -> {
                snackbarHostState.showSnackbar("Payment updated successfully")
                isEditMode = false
                viewModel.clearUpdateState()
            }
            is PaymentUpdateState.Error -> {
                snackbarHostState.showSnackbar((updateState as PaymentUpdateState.Error).error.userFriendlyMessage)
                viewModel.clearUpdateState()
            }
            else -> {}
        }
    }

    // Handle cancel state changes
    LaunchedEffect(cancelState) {
        when (cancelState) {
            is PaymentCancelState.Success -> {
                snackbarHostState.showSnackbar("Payment cancelled successfully")
                viewModel.clearCancelState()
                onNavigateBack()
            }
            is PaymentCancelState.Error -> {
                snackbarHostState.showSnackbar((cancelState as PaymentCancelState.Error).error.userFriendlyMessage)
                viewModel.clearCancelState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = if (isEditMode) "Edit Payment" else "Payment Detail",
                icon = Icons.Default.Payment,
                desc = "Payment Detail",
                onBackClick = {
                    if (isEditMode) {
                        isEditMode = false
                    } else {
                        onNavigateBack()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            when (val state = detailState) {
                is PaymentDetailUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payment selected")
                    }
                }

                is PaymentDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PaymentDetailUiState.Error -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ErrorBanner(
                            message = state.error.userFriendlyMessage,
                            onRetry = { viewModel.retryPaymentDetail() }
                        )
                        if (isEditMode) {
                            PaymentEditContent(
                                payment = state.payment,
                                isLoading = updateState is PaymentUpdateState.Loading,
                                onSave = { request ->
                                    viewModel.updatePayment(state.payment.id, request)
                                },
                                onCancel = { isEditMode = false },
                                onCancelPayment = { viewModel.cancelPayment(state.payment.id) }
                            )
                        } else {
                            PaymentDetailContent(
                                payment = state.payment,
                                onEditClick = { isEditMode = true },
                                onCancelPayment = { viewModel.cancelPayment(state.payment.id) },
                                isCancelLoading = cancelState is PaymentCancelState.Loading
                            )
                        }
                    }
                }

                is PaymentDetailUiState.Success -> {
                    if (isEditMode) {
                        PaymentEditContent(
                            payment = state.payment,
                            isLoading = updateState is PaymentUpdateState.Loading,
                            onSave = { request ->
                                viewModel.updatePayment(state.payment.id, request)
                            },
                            onCancel = { isEditMode = false },
                            onCancelPayment = { viewModel.cancelPayment(state.payment.id) }
                        )
                    } else {
                        PaymentDetailContent(
                            payment = state.payment,
                            onEditClick = { isEditMode = true },
                            onCancelPayment = { viewModel.cancelPayment(state.payment.id) },
                            isCancelLoading = cancelState is PaymentCancelState.Loading
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun PaymentDetailContent(
    payment: PaymentUiModel,
    onEditClick: () -> Unit,
    onCancelPayment: () -> Unit,
    isCancelLoading: Boolean
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Payment") },
            text = { Text("Are you sure you want to cancel this payment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelPayment()
                    }
                ) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Payment Amount Card with Edit button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = payment.amount,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = payment.transactionType.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (payment.transactionType.lowercase() == "credit")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
                // Edit button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Payment",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
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
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "Payment ID", value = "#${payment.id}")
                DetailRow(label = "Payment Method", value = payment.paymentMethod)
                DetailRow(label = "Reference Number", value = payment.refNumber)
                DetailRow(label = "Transaction Type", value = payment.transactionType)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customer Details Card
        payment.customer.let { customer ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text(
                            text = "Customer Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Name", value = customer.businessName.ifEmpty { customer.name })
                    if (customer.email.isNotEmpty()) {
                        DetailRow(label = "Email", value = customer.email)
                    }
                    if (customer.mobile.isNotEmpty()) {
                        DetailRow(label = "Phone", value = customer.mobile)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sale Details Card (if applicable)
        payment.sale?.let { sale ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text(
                            text = "Sale Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Invoice Number", value = sale.invoiceNumber)
                    DetailRow(label = "Sale Date", value = sale.saleDate)
                    DetailRow(label = "Total Amount", value = sale.totalAmount)
                    DetailRow(label = "Status", value = sale.invoiceStatus)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Received By Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Received By",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "Name", value = payment.receivedBy.name)
                DetailRow(label = "Role", value = payment.receivedBy.role)
                if (payment.receivedBy.mobile.isNotEmpty()) {
                    DetailRow(label = "Phone", value = payment.receivedBy.mobile)
                }
            }
        }

        // Denomination Details (if applicable)
        payment.denomination?.let { denom ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Denomination Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Calculate total
                    var total = 0

                    if (denom.denom2000 > 0) {
                        val amount = 2000 * denom.denom2000
                        total += amount
                        DenominationRow(denomination = 2000, count = denom.denom2000, amount = amount)
                    }
                    if (denom.denom500 > 0) {
                        val amount = 500 * denom.denom500
                        total += amount
                        DenominationRow(denomination = 500, count = denom.denom500, amount = amount)
                    }
                    if (denom.denom200 > 0) {
                        val amount = 200 * denom.denom200
                        total += amount
                        DenominationRow(denomination = 200, count = denom.denom200, amount = amount)
                    }
                    if (denom.denom100 > 0) {
                        val amount = 100 * denom.denom100
                        total += amount
                        DenominationRow(denomination = 100, count = denom.denom100, amount = amount)
                    }
                    if (denom.denom50 > 0) {
                        val amount = 50 * denom.denom50
                        total += amount
                        DenominationRow(denomination = 50, count = denom.denom50, amount = amount)
                    }
                    if (denom.denom20 > 0) {
                        val amount = 20 * denom.denom20
                        total += amount
                        DenominationRow(denomination = 20, count = denom.denom20, amount = amount)
                    }
                    if (denom.denom10 > 0) {
                        val amount = 10 * denom.denom10
                        total += amount
                        DenominationRow(denomination = 10, count = denom.denom10, amount = amount)
                    }
                    if (denom.denom5 > 0) {
                        val amount = 5 * denom.denom5
                        total += amount
                        DenominationRow(denomination = 5, count = denom.denom5, amount = amount)
                    }
                    if (denom.denom2 > 0) {
                        val amount = 2 * denom.denom2
                        total += amount
                        DenominationRow(denomination = 2, count = denom.denom2, amount = amount)
                    }
                    if (denom.denom1 > 0) {
                        val amount = 1 * denom.denom1
                        total += amount
                        DenominationRow(denomination = 1, count = denom.denom1, amount = amount)
                    }

                    // Other payment methods
                    if (denom.card > 0) {
                        total += denom.card
                        DetailRow(label = "Card", value = "₹${denom.card}")
                    }
                    if (denom.upi > 0) {
                        total += denom.upi
                        DetailRow(label = "UPI", value = "₹${denom.upi}")
                    }
                    if (denom.netBanking > 0) {
                        total += denom.netBanking
                        DetailRow(label = "Net Banking", value = "₹${denom.netBanking}")
                    }
                    if (denom.cheque > 0) {
                        total += denom.cheque
                        DetailRow(label = "Cheque", value = "₹${denom.cheque}")
                    }
                    if (denom.credit > 0) {
                        total += denom.credit
                        DetailRow(label = "Credit", value = "₹${denom.credit}")
                    }

                    // Total Amount
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹$total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Cancel Payment Button
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = { showCancelDialog = true },
            enabled = !isCancelLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            if (isCancelLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Cancel Payment")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentEditContent(
    payment: PaymentUiModel,
    isLoading: Boolean,
    onSave: (UpdatePaymentRequest) -> Unit,
    onCancel: () -> Unit,
    onCancelPayment: () -> Unit
) {
    // Parse amount from formatted string (e.g., "₹ 1,600.00" -> 1600.00)
    val initialAmount = payment.amount
        .replace("₹", "")
        .replace(",", "")
        .trim()
        .toDoubleOrNull() ?: 0.0

    var amount by remember { mutableStateOf(initialAmount.toString()) }
    var paymentMethod by remember { mutableStateOf(payment.paymentMethod) }
    var refNumber by remember { mutableStateOf(if (payment.refNumber == "—") "" else payment.refNumber) }

    // Denomination fields
    var denom2000 by remember { mutableIntStateOf(payment.denomination?.denom2000 ?: 0) }
    var denom500 by remember { mutableIntStateOf(payment.denomination?.denom500 ?: 0) }
    var denom200 by remember { mutableIntStateOf(payment.denomination?.denom200 ?: 0) }
    var denom100 by remember { mutableIntStateOf(payment.denomination?.denom100 ?: 0) }
    var denom50 by remember { mutableIntStateOf(payment.denomination?.denom50 ?: 0) }
    var denom20 by remember { mutableIntStateOf(payment.denomination?.denom20 ?: 0) }
    var denom10 by remember { mutableIntStateOf(payment.denomination?.denom10 ?: 0) }
    var denom5 by remember { mutableIntStateOf(payment.denomination?.denom5 ?: 0) }
    var denom2 by remember { mutableIntStateOf(payment.denomination?.denom2 ?: 0) }
    var denom1 by remember { mutableIntStateOf(payment.denomination?.denom1 ?: 0) }
    var card by remember { mutableIntStateOf(payment.denomination?.card ?: 0) }
    var upi by remember { mutableIntStateOf(payment.denomination?.upi ?: 0) }
    var netBanking by remember { mutableIntStateOf(payment.denomination?.netBanking ?: 0) }
    var cheque by remember { mutableIntStateOf(payment.denomination?.cheque ?: 0) }
    var credit by remember { mutableIntStateOf(payment.denomination?.credit ?: 0) }

    var paymentMethodExpanded by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val paymentMethods = listOf("Card", "UPI", "Net Banking", "Cash on Delivery", "Credit", "Cheque")

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Payment") },
            text = { Text("Are you sure you want to cancel this payment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelPayment()
                    }
                ) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Amount Field
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Payment Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("₹") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = paymentMethodExpanded,
                    onExpandedChange = { paymentMethodExpanded = !paymentMethodExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentMethodExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = paymentMethodExpanded,
                        onDismissRequest = { paymentMethodExpanded = false }
                    ) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    paymentMethod = method
                                    paymentMethodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reference Number
                OutlinedTextField(
                    value = refNumber,
                    onValueChange = { refNumber = it },
                    label = { Text("Reference Number (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Denomination Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Denomination Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Cash denominations
                Text(
                    text = "Cash",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "₹2000",
                        value = denom2000,
                        onValueChange = { denom2000 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹500",
                        value = denom500,
                        onValueChange = { denom500 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹200",
                        value = denom200,
                        onValueChange = { denom200 = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "₹100",
                        value = denom100,
                        onValueChange = { denom100 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹50",
                        value = denom50,
                        onValueChange = { denom50 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹20",
                        value = denom20,
                        onValueChange = { denom20 = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "₹10",
                        value = denom10,
                        onValueChange = { denom10 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹5",
                        value = denom5,
                        onValueChange = { denom5 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "₹2",
                        value = denom2,
                        onValueChange = { denom2 = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "₹1",
                        value = denom1,
                        onValueChange = { denom1 = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Other payment methods
                Text(
                    text = "Other Payment Methods",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "Card",
                        value = card,
                        onValueChange = { card = it },
                        modifier = Modifier.weight(1f),
                        isAmount = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "UPI",
                        value = upi,
                        onValueChange = { upi = it },
                        modifier = Modifier.weight(1f),
                        isAmount = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "Net Banking",
                        value = netBanking,
                        onValueChange = { netBanking = it },
                        modifier = Modifier.weight(1f),
                        isAmount = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DenominationEditField(
                        label = "Cheque",
                        value = cheque,
                        onValueChange = { cheque = it },
                        modifier = Modifier.weight(1f),
                        isAmount = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DenominationEditField(
                        label = "Credit",
                        value = credit,
                        onValueChange = { credit = it },
                        modifier = Modifier.weight(1f),
                        isAmount = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                val request = UpdatePaymentRequest(
                    amount = amount.toDoubleOrNull(),
                    paymentMethod = paymentMethod,
                    refNumber = refNumber.ifBlank { null },
                    denomination = DenominationRequest(
                        denom2000 = denom2000,
                        denom500 = denom500,
                        denom200 = denom200,
                        denom100 = denom100,
                        denom50 = denom50,
                        denom20 = denom20,
                        denom10 = denom10,
                        denom5 = denom5,
                        denom2 = denom2,
                        denom1 = denom1,
                        card = card.toDouble(),
                        upi = upi.toDouble(),
                        netBanking = netBanking.toDouble(),
                        cheque = cheque.toDouble(),
                        credit = credit.toDouble()
                    )
                )
                onSave(request)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cancel Edit Button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Cancel Edit")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cancel Payment Button
        OutlinedButton(
            onClick = { showCancelDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            enabled = !isLoading
        ) {
            Text("Cancel Payment")
        }
    }
}

@Composable
private fun DenominationEditField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isAmount: Boolean = false
) {
    OutlinedTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = { newValue ->
            onValueChange(newValue.toIntOrNull() ?: 0)
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        prefix = if (isAmount) {{ Text("₹") }} else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        singleLine = true
    )
}

@Composable
private fun DenominationRow(denomination: Int, count: Int, amount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "₹$denomination × $count",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "= ₹$amount",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
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
fun PaymentDetailErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.Red)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}
