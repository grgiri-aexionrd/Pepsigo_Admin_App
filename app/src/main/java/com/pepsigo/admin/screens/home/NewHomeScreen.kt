package com.pepsigo.admin.screens.home

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import com.pepsigo.admin.constants.modalDrawerGroups
import com.pepsigo.admin.model.DrawerItem
import com.pepsigo.admin.ui.theme.inversePrimaryLight
import com.pepsigo.admin.utils.NotificationPermissionManager
import com.pepsigo.admin.utils.NotificationPermissionRequester
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomeScreen(
    backStackEntry: NavBackStackEntry,
    viewModel: LogoutViewModel,
    onLogoutClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onDrawerItemClicked: (DrawerItem) -> Unit
){

    val lifecycle = backStackEntry.lifecycle

    var isVisible by rememberSaveable { mutableStateOf(false) }
    var mapLoaded by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isVisible = true
                }

                Lifecycle.Event.ON_PAUSE -> {
                    isVisible = false
                    mapLoaded = false // 🔑 critical for masking
                }

                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }


    val metricsState by viewModel.metricsState.collectAsState()
    val mapState by viewModel.mapState.collectAsState()        //🔥 map state isolated
    val screenState by viewModel.screenState.collectAsState()  // loading/refresh
    val refreshState = rememberPullToRefreshState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button to close drawer if open
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
    // Ensure drawer starts closed every time HomeScreen appears
    LaunchedEffect(Unit) {
        drawerState.snapTo(DrawerValue.Closed)
    }

    val context = LocalContext.current
    val hasAsked by NotificationPermissionManager.hasAskedPermission(context)
        .collectAsState(initial = false)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasAsked) {
        NotificationPermissionRequester(
            onComplete = {
                // Mark as asked → Never ask again
                scope.launch {
                    NotificationPermissionManager.setAsked(context, true)
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Disable gesture to open drawer, only open via menu icon, scrim tap is also disabled
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Manjunatha Agency",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    HorizontalDivider()

                    Spacer(Modifier.height(8.dp))

                    // ✅ Dynamically create drawer items from the centralized list
                    DrawerContentList(
                        modalDrawerGroups = modalDrawerGroups,
                        onItemClicked = { item ->
                            scope.launch { drawerState.close()
                                onDrawerItemClicked(item)
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))
                    // Logout item
                    Box (
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Logout(
                            viewModel = viewModel,
                            onLogoutClicked = onLogoutClicked
                        )
                    }

                }
            }
        },
    ){
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = {
                        Text("DashBoard")
                    },
                    navigationIcon = {
                        // IconButton to open the drawer
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onProfileClicked
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }
                    }

                )
            },
            containerColor = inversePrimaryLight.copy(alpha = 0.35f),
        ){ innerPadding ->
            // 🔥 Pull to Refresh MUST wrap ONLY the content, not the drawer or scaffold
            PullToRefreshBox(
                isRefreshing = screenState.isRefreshing,
                onRefresh = {  viewModel.refreshDashboard()  },
                state = refreshState,
                modifier = Modifier.padding(innerPadding)
            ){
                Box(Modifier
//                    .background(
//                        color = inversePrimaryLight.copy(alpha = 0.35f)
//                        )
                    .fillMaxSize()) {

                    if (isVisible) {
                        NewHomeScreenContent(
                            metricsState = metricsState,
                            mapState = mapState,
                            mapLoaded = mapLoaded,
                            onMapLoaded = { mapLoaded = true }
                        )
                    }

                    // 🧠 Mask teardown / loading gap
//                    if (!mapLoaded) {
//                        MapPlaceholder()
//                    }
                }

            }

        }

    }

}

@Composable
fun Logout(
    viewModel: LogoutViewModel,
    onLogoutClicked: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.logoutSuccess.collect { success ->
            if (success) onLogoutClicked()
        }
    }

    Button(
        onClick = {
            // Handle logout logic here
            viewModel.logout()
        },
        modifier = Modifier
            .padding(16.dp)
            .size(125.dp,50.dp)

    ) {
        Text(text = "Logout",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MapPlaceholder() {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}