package com.pepsigo.admin.screens.vendors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreen(
    viewModel: VendorViewModel,
    onPickLocation: () -> Unit,
    onNavigateBackToHome: () -> Unit
) {
    val vendorState by  viewModel.vendorUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        topBar = {
            VendorTopAppBar(
                onBackClick = { viewModel.getVendors() },
                onNavigateBackToHome = onNavigateBackToHome,
                vendorUiState = vendorState
            )
        },
        floatingActionButton = {
            if (vendorState is VendorUiState.VendorList) {
                FloatingActionButton(onClick = { viewModel.addVendors() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Customer")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = (vendorState as? VendorUiState.VendorList)?.isError ?: false
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .background(
                    color = inversePrimaryLight.copy(alpha = 0.35f)
                )
                .padding(innerPadding)
                .fillMaxSize()
        ){
            when (val state = vendorState) {
                is VendorUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
                    }

                }
                is VendorUiState.VendorList -> {
                    VendorListScreen(
                        vendors = state.vendors,
                        onEditVendor = {  vendor -> viewModel.editVendor(vendor) },
                        onToggle = {  id -> viewModel.toggleVendorStatus(id) },
                        modifier = Modifier.padding(16.dp)
                    )
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
                is VendorUiState.AddEditVendor -> {
                    VendorFormScreen(
                        viewModel = viewModel,
                        state = state ,
                        onPickLocation = onPickLocation,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is VendorUiState.Error -> {
                    val error = (vendorState as VendorUiState.Error).message
                    Text(
                        text = "Error: ${error.userFriendlyMessage}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {}

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorTopAppBar(
    onBackClick: () -> Unit,
    onNavigateBackToHome: () -> Unit,
    vendorUiState: VendorUiState
) {
    TopAppBar(
        title = {
            Text(
                when (vendorUiState) {
                    is VendorUiState.Loading,
                    is VendorUiState.VendorList -> "Vendors"
                    is VendorUiState.AddEditVendor ->
                        if (vendorUiState.isEdit) "Edit Vendor"
                        else "Add Vendor"

                    else -> ""
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                when (vendorUiState) {
                    is VendorUiState.VendorList -> onNavigateBackToHome()
                    else -> onBackClick()
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}