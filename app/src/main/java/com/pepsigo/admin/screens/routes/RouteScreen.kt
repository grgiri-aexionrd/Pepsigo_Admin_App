package com.pepsigo.admin.screens.routes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.RouteUiState
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun RouteScreen(
    viewModel: RouteViewModel,
    onNavigateBackToHome: () -> Unit
) {
    val routeUiState by viewModel.routes.collectAsState()
    val deliveryExec by viewModel.deliveryExecutives.collectAsState()
    val sheetState by viewModel.sheetState.collectAsState()

    val formState by viewModel.formState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing = routeUiState is RouteUiState.Loading

    Scaffold (
        topBar = {
            RouteTopAppBar(onBackToList = { viewModel.getRouteList() },
                onNavigateBackToHome = onNavigateBackToHome,
                routeUiState = routeUiState)
        },
            floatingActionButton = {
                if (routeUiState is RouteUiState.RoutesList) {
                    FloatingActionButton(onClick = { viewModel.addRoute()}) {
                        Icon(Icons.Default.Add, contentDescription = "Add Route")
                    }
                }
            },
        snackbarHost = {
            SnackbarHost(snackbarHostState){data ->
                val isError = when(routeUiState) {
                    is RouteUiState.RoutesList -> (routeUiState as RouteUiState.RoutesList).snackbarError
                    is RouteUiState.AddRoute -> (routeUiState as RouteUiState.AddRoute).snackbarError
                    is RouteUiState.EditRoute -> (routeUiState as RouteUiState.EditRoute).snackbarError
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
    ){innerPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {  viewModel.refreshRouteScreen()  },
            state = refreshState,
//            modifier = Modifier.padding(innerPadding)
        ) {
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                color = Color.Transparent
            ) {
                when (val state = routeUiState) {
                    is RouteUiState.RoutesList -> {
                        if (state.routes.isEmpty()) {
                            RouteErrorDetail(errorMessage = state.snackbarMessage)
                        } else {
                            RoutesListScreen(
                                routes = state.routes,
                                onAssignRouteClick = { viewModel.onAssignRouteClick(it) },
                                onEditRoute = { viewModel.editRoute(it) },
                                onToggle = { route -> viewModel.toggleRoute(route) },
//                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        state.snackbarMessage?.let { message ->
                            LaunchedEffect(message) {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.clearSnackbar()
                            }
                        }
                    }

                    is RouteUiState.AddRoute -> {
                        RouteFormScreen(
                            formState = formState,
                            onNameChange = { viewModel.updateRouteName(it) },
                            onLocationToggle = { viewModel.toggleLocation(it) },
                            onLocationMove = { from, to -> viewModel.moveLocation(from, to) },
                            onSave = { viewModel.saveRoute() },
                            modifier = Modifier.padding(innerPadding)
                        )
                        state.snackbarMessage?.let { message ->
                            LaunchedEffect(message) {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.clearSnackbar()
                            }
                        }
                        BackHandler { viewModel.getRouteList() }

                    }

                    is RouteUiState.EditRoute -> {
                        RouteFormScreen(
                            formState = formState,
                            onNameChange = { viewModel.updateRouteName(it) },
                            onLocationToggle = { viewModel.toggleLocation(it) },
                            onLocationMove = { from, to -> viewModel.moveLocation(from, to) },
                            onSave = { viewModel.saveRoute(state.routeId) },
                            modifier = Modifier.padding(innerPadding)
                        )
                        state.snackbarMessage?.let { message ->
                            LaunchedEffect(message) {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.clearSnackbar()
                            }
                        }
                        BackHandler { viewModel.getRouteList() }
                    }

                    is RouteUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(innerPadding)
                            )
                        }
                    }

                    else -> {}
                }

                // Assign route Bottom Sheet
                if (sheetState.isVisible && sheetState.assignRoute != null) {
                    AssignRouteBottomSheet(
                        route = sheetState.assignRoute!!,
                        delExec = deliveryExec,
                        assignError = sheetState.assignError,
                        updateSelected = { viewModel.updateSelectedDeliveryExecutive(it) },
                        onAssignSave = { route, exec ->
                            viewModel.assignDeliveryExecutiveToRoute(
                                route,
                                exec
                            )
                        },
                        onDismiss = { viewModel.onSheetDismiss() }
                    )
                }
            }
        }

}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteTopAppBar(onBackToList: () -> Unit,
                   onNavigateBackToHome: () -> Unit,
                   routeUiState: RouteUiState) {
    TopAppBar(
        title = {
            Text(
                when ( routeUiState) {
                    is RouteUiState.RoutesList,
                    is RouteUiState.Loading -> "Routes"
                    is RouteUiState.AddRoute -> "Add Route"
                    is RouteUiState.EditRoute -> "Edit Route"
                    else -> ""
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
                },
        navigationIcon = {
            IconButton(onClick = {
                when (routeUiState) {
                    is RouteUiState.AddRoute,
                    is RouteUiState.EditRoute -> onBackToList()

                    else -> onNavigateBackToHome()
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

