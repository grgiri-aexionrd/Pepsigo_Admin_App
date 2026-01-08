package com.pepsigo.admin.screens.deliveryExecutive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.ui.theme.inversePrimaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeliveryExecutiveScreen(
    viewModel: DeliveryExecutiveViewModel,
    onNavigateBackToHome: () -> Unit
) {
    val deliveryState by viewModel.deliveryUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            DeliveryExecutiveTopAppBar(
                onBackClick = {
                    val current = deliveryState
                    if (current is DeliveryExecutiveUiState.AddDelForm || current is DeliveryExecutiveUiState.EditDelForm) {
                        viewModel.getDeliveryExecutives()   // go back to list screen
                    } else {
                        onNavigateBackToHome()             // normal back navigation
                    }
                }
            )
        },
        floatingActionButton = {
            if (deliveryState is DeliveryExecutiveUiState.DeliveryExecutiveList) {
                FloatingActionButton(onClick = { viewModel.addDeliveryExecutive() }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add Delivery Executive")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = deliveryState){
                is DeliveryExecutiveUiState.Loading->{

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                is DeliveryExecutiveUiState.DeliveryExecutiveList -> {
                    DeliveryExecutiveListScreen(
                        deliveryExecutives = state.deliveryExecutives,
                        onEdit = {id -> viewModel.getDeliveryExecutiveById(id) },
                        onToggle = {exec -> viewModel.toggleDeliveryExecutiveStatus(exec) },
//                        onRefresh = { viewModel.getDeliveryExecutives() }
                        modifier = Modifier.padding(16.dp)
                    )
                    state.snackbarMessage?.let { message ->
                        LaunchedEffect(message) {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short
                            )
//                            viewModel::clearSnackbarMessage
                            viewModel.clearSnackbarMessage()
                        }
                    }
                }
                is DeliveryExecutiveUiState.AddDelForm -> {
                    DeliveryExecutiveAddScreen(
                        viewModel =viewModel,
                        state = state,
                        onSave = { form -> viewModel.saveDeliveryExecutive(form) },
                        modifier = Modifier.padding(16.dp)
                    )
                    state.snackbarMessage?.let { message ->
                        LaunchedEffect(message) {
                            val job = launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Indefinite // control manually
                                )
                            }

                            // Wait custom duration (e.g., 1.5s)
                            delay(1000)
                            snackbarHostState.currentSnackbarData?.dismiss()

                            viewModel.clearSnackbarMessage()
                        }
                    }
                }
            is DeliveryExecutiveUiState.EditDelForm -> {
                DeliveryExecutiveEditScreen(
                    viewModel = viewModel,
                    state = state ,
                    onSave = { form -> viewModel.updateDeliveryExecutive(form)  },
                    modifier = Modifier.padding(16.dp)
                )
                state.snackbarMessage?.let { message ->
                    LaunchedEffect(message) {
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
//                            viewModel::clearSnackbarMessage
                        viewModel.clearSnackbarMessage()
                    }
                }
            }

            }

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryExecutiveTopAppBar(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Icon(Icons.Default.DeliveryDining, contentDescription = "Delivery Executive ")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Delivery Executives")
            }
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
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




