package com.pepsigo.admin.screens.createPurchaseScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.constants.DateSelectionMode
import com.pepsigo.admin.screens.commonComponents.DockedDatePicker
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.screens.reports.DropDownErrorCard
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseScreen(
    viewModel: CreatePurchaseViewModel,
    onNavigateBack: () -> Unit
) {
    val createPurchaseState by viewModel.createPurchase.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var purchaseDate by remember { mutableStateOf<String?>(null) }
    val refreshState = rememberPullToRefreshState()

    // BottomSheet
    var showBottomSheet by remember { mutableStateOf(false) }
    fun closeBottomSheet() {
        showBottomSheet = false
    }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    var invoiceNumber by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.create_purchase),
                icon = Icons.Default.ShoppingCart,
                desc = "Create Purchase",
                onBackClick = { onNavigateBack()}
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState){ data ->

                Snackbar(
                    snackbarData = data,
                    containerColor = if (createPurchaseState.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (createPurchaseState.isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                )

            }
        },
        bottomBar = {
            Button(
                onClick = {
                    Log.d("CreatePurchaseScreen", "SubmitPurchaseScreen: ${createPurchaseState.selectedVendor} $invoiceNumber $purchaseDate ${createPurchaseState.addEditItemDetails} ")
                    viewModel.submitPurchase(
                    selectedVendor = createPurchaseState.selectedVendor,
                    invoiceNumber = invoiceNumber,
                    purchaseDate = purchaseDate,
                    addEditItemDetails = createPurchaseState.addEditItemDetails

                ) },
                enabled = createPurchaseState.addEditItemDetails.isNotEmpty(),
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
                    .fillMaxWidth()

            ) {
                Text(text = stringResource(id = R.string.submit_purchase))
            }

        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = createPurchaseState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                //vendor dropdown
                item {
                    Text(
                        text = stringResource(id = R.string.vendor_dropdown),

                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (createPurchaseState.vendorError != null) {
                        DropDownErrorCard(error = createPurchaseState.vendorError!!)
                    } else {
                        DropDown(
                            dropDown = createPurchaseState.vendorDropDown,
                            error = createPurchaseState.submitErrors.vendorError,
                            label = stringResource(id = R.string.select_vendor),
                            selected = createPurchaseState.selectedVendor,
                            onSelected = { viewModel.updateSelectedVendor(it) },
                            labelExtractor = { it.name },
                            isCreatePurchase = true
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // invoice details
                item {
                    Text(
                        text = stringResource(id = R.string.invoice_details),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.invoice_number_optional),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        placeholder = { Text(text = stringResource(id = R.string.enter_invoice_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // purchase date
                item {
                    Text(
                        text = stringResource(id = R.string.purchase_date),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DockedDatePicker(
                        label = "",
                        error = createPurchaseState.submitErrors.purchaseDateError,
                        modifier = Modifier.fillMaxWidth(),
                        onDateSelected = { purchaseDate = it },
                        mode = DateSelectionMode.PAST_OR_TODAY
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // purchased items
                item {
                    Text(
                        text = stringResource(id = R.string.products),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    if (createPurchaseState.addEditItemDetails.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
//                    .padding(innerPadding)
//                        .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(4.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.icons8_clear_shopping_cart_48),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(12.dp))
                                Text("No items added yet")
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Tap Add Item to include purchase items.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp)   // set max height to avoid nested scroll crash
                        ) {
                            itemsIndexed(createPurchaseState.addEditItemDetails) { index, item ->
                                AddPurchaseItemCard(
                                    item = item,
                                    onEdit = {
                                        viewModel.editPurchaseItem(index)
                                        showBottomSheet = true
                                    },
                                    onDelete = { viewModel.deletePurchaseItem(index) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                    }
                }

                item {
                    // “Add Item” Button inside card
                    FilledTonalButton(
                        onClick = {
                            viewModel.clearBottomSheet()
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.add_item))
                    }
                }
            }


            // BottomSheet
            AddItemBottomSheet(
                show = showBottomSheet,
                uiState = createPurchaseState,
                updateSelectedInventory = { viewModel.updateSelectedInventory(it) },
                onDismiss = { closeBottomSheet() },
                onSave = { selectedInv, qty, cost, sale, retail, expiry ->
                    Log.d(
                        "CreatePurchaseScreen",
                        "onSave: $selectedInv, $qty, $cost, $sale, $retail, $expiry"
                    )
                    val savedResult =
                        viewModel.savePurchaseItem(selectedInv, qty, cost, sale, retail, expiry)
                    if (savedResult) {
                        closeBottomSheet()
                    }
                }
            )

            createPurchaseState.snackbarMessage?.let { message ->
                LaunchedEffect(message) {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    viewModel.clearSnackbarMessage()
                }
            }
        }
    }
}











