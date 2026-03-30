package com.pepsigo.admin.screens.payment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.domainLayer.DenominationInput
import com.pepsigo.admin.screens.commonComponents.InlineRetryError
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.screens.commonComponents.SearchDropDown
import com.pepsigo.admin.screens.reports.DropDownList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePaymentEntryScreen(
    state: MakePaymentUiState,
    onBack: () -> Unit,
    onSelected: (DropDownList?) -> Unit,
    onCustomerSearchChange: (String) -> Unit,
    onVendorSearchChange: (String) -> Unit,
    paymentAmountChange: (String) -> Unit,
    paymentMethodChange: (String) -> Unit,
    transactionTypeChange: (String) -> Unit,
    refNumberChange: (String) -> Unit,
    onCashChange: ((CashDenomination) -> CashDenomination) -> Unit,
    onDigitalChange: ((DigitalDenomination) -> DigitalDenomination) -> Unit,
    validatePayment: () -> Unit,
    onRetry: () -> Unit,
    onPartyTypeChanged: (PartyType) -> Unit
) {
    val filteredCustomers by remember(
        state.customers,
        state.customerSearchQuery
    ) {
        derivedStateOf {
            if (state.customerSearchQuery.isBlank()) {
                state.customers.take(50)
            } else {
                state.customers
                    .filter {
                        it.name.contains(state.customerSearchQuery, true)
                    }
                    .take(50)
            }
        }
    }

    val filteredVendors by remember(
        state.vendors,
        state.vendorSearchQuery
    ) {
        derivedStateOf {
            if (state.vendorSearchQuery.isBlank()) {
                state.vendors.take(50)
            } else {
                state.vendors
                    .filter {
                        it.name.contains(state.vendorSearchQuery, true)
                    }
            }
        }

    }
    Log.d("MakePaymentEntryScreen", "filteredCustomers: $filteredCustomers")
    Log.d("MakePaymentEntryScreen", "filteredVendors: $filteredVendors")



    var paymentMethodExpanded by remember { mutableStateOf(false) }

    var transactionTypeExpanded by remember { mutableStateOf(false) }


    val paymentMethods = listOf("Card", "UPI", "Net Banking", "Cash on Delivery", "Credit", "Cheque")
    val transactionTypes = listOf("credit", "debit")

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.make_payment),
                icon = Icons.Default.Add,
                desc = stringResource(id = R.string.make_payment),
                onBackClick = onBack
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

                /* ---------- CONTEXT ---------- */

                state.saleId?.let {
                    Text("Sale ID: $it", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Sale Amount: ${state.totalAmount ?: "-"}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                }

                state.purchaseId?.let {
                    Text("Purchase ID: $it", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Purchase Amount: ${state.totalAmount ?: "-"}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                }

                // Customer Selection Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Business Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        if (!state.isCustomerEditable) {
                            // Show read-only customer field when saleId or purchaseId is provided
                            OutlinedTextField(
                                value = state.selectedCustomer?.name.orEmpty(),
                                onValueChange = {},
                                label = { Text("Customer") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            state.customerError?.let{
                                InlineRetryError(
                                    error = it,
                                    retryLoading = state.retryLoading,
                                    onRetry = onRetry
                                )

                            }

                            state.vendorError?.let{
                                InlineRetryError(
                                    error = it,
                                    retryLoading = state.retryLoading,
                                    onRetry = onRetry
                                )
                            }

                        } else {

                            PartyTypeRadio(
                                selected = state.partyType,
                                enabled = true,
                                onSelected = onPartyTypeChanged
                            )
                            // Show searchable dropdown for customer selection
                            // modify this to show customers, vendor list based on user selection. of partytype
                            when (state.partyType) {
                                PartyType.CUSTOMER -> {
                                    SearchDropDown(
                                        filteredDropDown = filteredCustomers,
                                        label = "Select Customer",
                                        searchQuery = state.customerSearchQuery,
                                        selected = state.selectedCustomer,
                                        onSelected = {  customer ->
                                            onSelected(customer)
                                        },
                                        onSearchChange = { onCustomerSearchChange(it) },
                                        labelExtractor = { user ->
                                            user.name
                                        }
                                    )
                                    state.customerError?.let{
                                        InlineRetryError(
                                            error = it,
                                            retryLoading = state.retryLoading,
                                            onRetry = onRetry
                                        )
                                    }
                                }
                                PartyType.VENDOR -> {
                                    SearchDropDown(
                                        filteredDropDown = filteredVendors,
                                        label = "Select Vendor",
                                        searchQuery = state.vendorSearchQuery,
                                        selected = state.selectedCustomer,
                                        onSelected = { vendor ->
                                            onSelected(vendor)
                                            },
                                        onSearchChange = {
                                            onVendorSearchChange(it)
                                        },
                                        labelExtractor = { user ->
                                            user.name
                                        }
                                    )
                                    state.vendorError?.let{
                                        InlineRetryError(
                                            error = it,
                                            retryLoading = state.retryLoading,
                                            onRetry = onRetry
                                        )

                                    }
                                }

                                }
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
                            text = "Payment Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Amount Field
                        OutlinedTextField(
                            value = state.paymentAmount,
                            onValueChange = {
                                paymentAmountChange(it)  },
                            isError = state.paymentAmountError,
                            label = { Text("Amount *") },
                            prefix = { Text("₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        state.error?.let{
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Payment Method Dropdown
                        ExposedDropdownMenuBox(
                            expanded = paymentMethodExpanded,
                            onExpandedChange = { paymentMethodExpanded = !paymentMethodExpanded }
                        ) {
                            OutlinedTextField(
                                value = state.paymentMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Payment Method *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentMethodExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = paymentMethodExpanded,
                                onDismissRequest = { paymentMethodExpanded = false }
                            ) {
                                paymentMethods.forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method) },
                                        onClick = {
                                            paymentMethodChange(method)
                                            paymentMethodExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transaction Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = transactionTypeExpanded,
                            onExpandedChange = { transactionTypeExpanded = !transactionTypeExpanded }
                        ) {
                            OutlinedTextField(
                                value = state.transactionType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Transaction Type *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transactionTypeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = transactionTypeExpanded,
                                onDismissRequest = { transactionTypeExpanded = false }
                            ) {
                                transactionTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            transactionTypeChange(type)
                                            transactionTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reference Number
                        OutlinedTextField(
                            value = state.refNumber,
                            onValueChange = { refNumberChange(it) },
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
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Denomination Details (Optional)",
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
                            DenominationField(
                                label = "₹2000",
                                value = state.cashDenomination.denom2000,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom2000 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹500",
                                value = state.cashDenomination.denom500,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom500 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹200",
                                value = state.cashDenomination.denom200,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom200 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DenominationField(
                                label = "₹100",
                                value = state.cashDenomination.denom100,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom100 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹50",
                                value = state.cashDenomination.denom50,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom50 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹20",
                                value = state.cashDenomination.denom20,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom20 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DenominationField(
                                label = "₹10",
                                value = state.cashDenomination.denom10,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom10 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹5",
                                value = state.cashDenomination.denom5,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom5 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DenominationField(
                                label = "₹2",
                                value = state.cashDenomination.denom2,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom2 = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DenominationField(
                                label = "₹1",
                                value = state.cashDenomination.denom1,
                                onValueChange = { newValue ->
                                    onCashChange {
                                        it.copy(denom1 = newValue)
                                    }
                                },
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
                            DigitalDenominationField(
                                label = "Card",
                                value = state.digitalDenomination.card,
                                onValueChange = { newValue ->
                                    onDigitalChange{
                                        it.copy(card = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DigitalDenominationField(
                                label = "UPI",
                                value = state.digitalDenomination.upi,
                                onValueChange = { newValue ->
                                    onDigitalChange{
                                        it.copy(upi = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DigitalDenominationField(
                                label = "Net Banking",
                                value = state.digitalDenomination.netBanking,
                                onValueChange = { newValue ->
                                    onDigitalChange{
                                        it.copy(netBanking = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DigitalDenominationField(
                                label = "Cheque",
                                value = state.digitalDenomination.cheque,
                                onValueChange = { newValue ->
                                    onDigitalChange{
                                        it.copy(cheque = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DigitalDenominationField(
                                label = "Credit",
                                value = state.digitalDenomination.credit,
                                onValueChange = { newValue ->
                                    onDigitalChange{
                                        it.copy(credit = newValue)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            validatePayment()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.paymentAmount.toDoubleOrNull() != null && (state.paymentAmount.toDoubleOrNull() ?: 0.0) >= 0.01
                    ) {
                        Text("Submit")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DenominationField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,

) {
    OutlinedTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = { newValue ->
            onValueChange(newValue.toIntOrNull() ?: 0)
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        singleLine = true
    )
}
@Composable
private fun DigitalDenominationField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    OutlinedTextField(
        value = if (value == 0.00) "" else value.toString(),
        onValueChange = { newValue ->
            onValueChange(newValue.toDoubleOrNull() ?: 0.00)
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        prefix =  { Text("₹") } ,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        singleLine = true
    )
}

@Composable
fun PartyTypeRadio(
    selected: PartyType,
    enabled: Boolean,
    onSelected: (PartyType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PartyType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
//                    .fillMaxWidth()
                    .clickable(enabled) { onSelected(type) }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selected == type,
                    onClick = null,
                    enabled = enabled
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = type.name.lowercase().replaceFirstChar { it.uppercase() }
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}






