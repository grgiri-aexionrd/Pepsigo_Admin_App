package com.pepsigo.admin.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.ui.theme.inversePrimaryLight


@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel,
    onPickLocation: () -> Unit,
    onNavigateBackToHome: () -> Unit
) {
    val customerState by viewModel.customerUiState.collectAsState()


    Scaffold (
        topBar = {
            CustomerTopAppBar(
                onBackClick = { viewModel.getCustomers() },
                onNavigateBackToHome = onNavigateBackToHome,
                customerUiState = customerState
            )
        },
        floatingActionButton = {
            if (customerState is CustomerUiState.CustomerList) {
                 FloatingActionButton(onClick = { viewModel.addCustomers() }) {
                     Icon(Icons.Default.Add, contentDescription = "Add Customer")
                 }
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
        ) {
            when (val state = customerState) {
                is CustomerUiState.CustomerList -> {
                    CustomerListScreen(
                        customers = state.customers,
                        onEditCustomer = { viewModel.editCustomer(it)  },
                        onToggle = {  id -> viewModel.toggleCustomerStatus(id) },
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is CustomerUiState.AddEditCustomer -> {
                    CustomerFormScreen(
                        form = state.form,
                        viewModel = viewModel,
                        locations = state.locations,
                        isEdit = state.isEdit,
                        onSave = { viewModel.saveCustomer(it) },
                        onCancel = { viewModel.getCustomers() },
                        onPickLocation = onPickLocation,
                        modifier = Modifier.padding(16.dp)
                    )
                    BackHandler {
                        viewModel.getCustomers()  // return to list state
                    }
                }
                is CustomerUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Loading...")
                    }
                }
                is CustomerUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: ${state.message.message}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTopAppBar(
    onBackClick: () -> Unit,
    onNavigateBackToHome: () -> Unit,
    customerUiState: CustomerUiState
) {
    TopAppBar(
        title = {
            Text(
                when (customerUiState) {
                    is CustomerUiState.Loading,
                    is CustomerUiState.CustomerList -> "Customers"
                    is CustomerUiState.AddEditCustomer ->
                        if (customerUiState.isEdit) "Edit Customer"
                        else "Add Customer"

                    else -> ""
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                when (customerUiState) {
                    is CustomerUiState.CustomerList -> onNavigateBackToHome()
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

@Preview(showBackground = true)
@Composable
fun CustomerScreenPreview() {

}

