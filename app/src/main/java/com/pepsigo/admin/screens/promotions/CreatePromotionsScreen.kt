package com.pepsigo.admin.screens.promotions

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.screens.commonComponents.SearchDropDown
import com.pepsigo.admin.screens.inventory.StatusChip
import com.pepsigo.admin.screens.purchase.PurchaseUiState
import com.pepsigo.admin.screens.reports.DropDownErrorCard
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun CreatePromotionsScreen(
    viewModel: CreatePromotionsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    // filtered customers
    val filteredCustomers by remember {
        derivedStateOf {
            if (state.customerSearchQuery.isBlank()) {
                state.customerDropDown.take(50)
            } else {
                state.customerDropDown
                    .filter {
                        it.name.contains(state.customerSearchQuery, true)
                    }
                    .take(50)
            }
        }
    }

    // filtered inventory
    val filteredInventory by remember {
        derivedStateOf {
            if (state.inventorySearchQuery.isBlank()) {
                state.inventoryDropDown.take(50)
            } else {
                state.inventoryDropDown
                    .filter {
                        it.name.contains(state.inventorySearchQuery, true)
                    }
                    .take(50)
            }
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.create_promotions),
                icon = Icons.Default.Campaign,
                desc = "Promotions",
                onBackClick = onNavigateBack,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = state.isError
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ){
        innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {   viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = refreshState
        ){
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                //customer dropdown
                SearchDropDown(
                    filteredDropDown = filteredCustomers,
                    error = state.selectedCustomerError,
                    label = stringResource(id = R.string.customer_dropdown),
                    searchQuery = state.customerSearchQuery,
                    selected = state.selectedCustomer,
                    onSelected = { viewModel.updateSelectedCustomer(it) },
                    onSearchChange = { viewModel.updateCustomerSearch(it) },
                    labelExtractor = { it.name },
                )
                if(state.selectedCustomerError){
                    Text(text = stringResource(id = R.string.dropdown_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // inventory dropdown
                SearchDropDown(
                    filteredDropDown = filteredInventory,
                    error = state.selectedInventoryError,
                    label = stringResource(id = R.string.product_dropdown),
                    searchQuery = state.inventorySearchQuery,
                    selected = state.selectedInventory,
                    onSelected = { viewModel.updateSelectedInventory(it) },
                    onSearchChange = { viewModel.updateInventorySearch(it) },
                    labelExtractor = { it.name },
                )
                if(state.selectedInventoryError){
                    Text(text = stringResource(id = R.string.dropdown_error),
                        color = MaterialTheme.colorScheme.error)
                }

                if(state.customerDropDownError !=null || state.inventoryDropDownError !=null ){
                    val error = state.customerDropDownError ?: state.inventoryDropDownError

                    error?.let {
                        DropDownErrorCard(it)
                    }
                }

                if(state.selectedInventory !=null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ProductDetailCard(
                        name = state.selectedInventory!!.name,
                        unit = state.selectedInventory!!.unit,
                        gst = state.selectedInventory!!.gstPercent,
                        enabled = state.selectedInventory!!.enabled
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AddPromotionCard(
                        quantity = detailState.quantity,
                        salePrice = detailState.salePrice,
                        isFreeProduct = detailState.isFreeProduct,
                        isLoading = detailState.isLoading,
                        qtyError = detailState.quantityError,
                        salePriceError = detailState.salePriceError,
                        onCheckedChange = { viewModel.onCheckedChange(it) } ,
                        onQuantityChange = { viewModel.onQuantityChange(it) },
                        onSalePriceChange = { viewModel.onSalePriceChange(it) },
                        onAddOfferClicked = { viewModel.addOffer()
                        Log.d("CreatePromotionsScreen", "onAddOfferClicked")
                        }
                    )

                    Text(text = stringResource(id = R.string.add_offer_info),
                        color = MaterialTheme.colorScheme.error
                    )

                }

            }
            state.snackbarMessage?.let { message ->
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

@Composable
fun ProductDetailCard(
    name: String,
    unit: String,
    gst: Double,
    enabled: Boolean
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){

            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = "Campaign Icon",
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column() {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Unit: ${unit} | GST: ${gst}%",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End) {
                StatusChip(isEnabled = enabled)
            }
        }

    }

}

@Composable
fun AddPromotionCard(
    quantity: String,
    salePrice: String,
    isFreeProduct: Boolean,
    isLoading: Boolean,
    qtyError: Boolean,
    salePriceError: Boolean,
    onQuantityChange: (String) -> Unit,
    onSalePriceChange: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onAddOfferClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //checkbox for freeProduct
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = stringResource(id = R.string.is_free_product))
                Spacer(modifier = Modifier.width(4.dp))
                Checkbox(
                    checked = isFreeProduct,
                    onCheckedChange = { onCheckedChange(it) }
                )
            }
            //quantity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.quantity))
                Spacer(modifier = Modifier.width(4.dp))
                TextField(
                    value = quantity,
                    onValueChange = { onQuantityChange(it) },
                    modifier = Modifier.wrapContentSize(),
                    enabled = isFreeProduct,
                    isError = qtyError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            }
            if (qtyError) {
                Text(
                    text = stringResource(id = R.string.quantity_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
            //sale price)

            //sale price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.sale_price))
                Spacer(modifier = Modifier.width(4.dp))
                TextField(
                    value = salePrice,
                    onValueChange = { onSalePriceChange(it) },
                    enabled = !isFreeProduct,
                    isError = salePriceError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            }
            if (salePriceError) {
                Text(
                    text = stringResource(id = R.string.quantity_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
            Button(
                onClick =  onAddOfferClicked,
                enabled = !isLoading
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text(text = stringResource(id = R.string.submit_promotion))
                }
            }

        }
    }

}

