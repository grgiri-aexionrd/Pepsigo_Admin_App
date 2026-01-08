package com.pepsigo.admin.screens.inventory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.R
import com.pepsigo.admin.screens.commonComponents.ReportTopAppBar
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val inventoryState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold (
        topBar = {
            ReportTopAppBar(
                label = stringResource(id = R.string.inventory),
                icon = Icons.Default.Inventory,
                desc = stringResource(id = R.string.inventory),
                onBackClick = {
                    when(inventoryState) {
                        is InventoryUiState.AddEditInventory -> viewModel.getInventories()
                        is InventoryUiState.InventoryDetails -> viewModel.onBackFromDetails()
                        else -> { onNavigateBack() }
                    }
                }
            )
        },
        floatingActionButton = {
            if (inventoryState is InventoryUiState.InventoryList) {
                FloatingActionButton(onClick = { viewModel.addInventory() } )
                {
                    Icon(Icons.Default.AddBox, contentDescription = stringResource(id = R.string.inventory))
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = when(inventoryState) {
                    is InventoryUiState.InventoryList -> (inventoryState as InventoryUiState.InventoryList).isError
                    is InventoryUiState.AddEditInventory -> (inventoryState as InventoryUiState.AddEditInventory).isError
                    is InventoryUiState.InventoryDetails -> (inventoryState as InventoryUiState.InventoryDetails).isError
                    else -> false
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        },
        containerColor = inversePrimaryLight.copy(alpha = 0.35f),
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            when (val state = inventoryState) {
                is InventoryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.loading))
                    }
                }

                is InventoryUiState.InventoryList -> {
                    InventoryListScreen(
                        inventoryItems = state.items,
                        onClick = { item ->
                            Log.d("InventoryScreen", "Clicked item: $item")
                            viewModel.getInventoryById(item)
                        },
                        onEdit = { item ->
                            Log.d("InventoryScreen", "Editing item: $item")
                            viewModel.updateInventory(item) },
                        onToggle = { id -> viewModel.toggleInventoryStatus(id) },
                        modifier = Modifier.padding(16.dp)
//                        onRefresh = { viewModel.getInventories() }
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

                is InventoryUiState.InventoryDetails -> {

                    Log.d("InventoryScreen", "Displaying details for item: ${state.itemDetails}")
                    InventoryDetailScreen(
                        inventoryDetail = state.itemDetails,
                        isError = state.isError,
//                        onBack = { viewModel.getInventories() },
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

                is InventoryUiState.AddEditInventory -> {
                    AddEditInventoryScreen(
                        viewModel = viewModel,
                        form = state.form,
                        isEdit = state.isEdit,
                        formErrors = state.formErrors,
//                        onFormChange = { /* viewModel.onFormChange(it)*/  },
                        onSave =  { form ->
                            Log.d("InventoryScreen", "Saving inventory: $form")
                            viewModel.onSaveInventory(form) } ,
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
            }


        }
    }
}