package com.pepsigo.admin

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pepsigo.admin.constants.Routes
import com.pepsigo.admin.data.UserPreferenceRepository
import com.pepsigo.admin.screens.deliveryExecutive.DeliveryExecutiveScreen
import com.pepsigo.admin.screens.commonComponents.MapScreen
import com.pepsigo.admin.screens.createPurchaseScreen.CreatePurchaseScreen
import com.pepsigo.admin.screens.createPurchaseScreen.CreatePurchaseViewModel
import com.pepsigo.admin.screens.customer.CustomerScreen
import com.pepsigo.admin.screens.customer.CustomerViewModel
import com.pepsigo.admin.screens.deliveryExecutive.DeliveryExecutiveViewModel
import com.pepsigo.admin.screens.home.LogoutViewModel
import com.pepsigo.admin.screens.inventory.InventoryScreen
import com.pepsigo.admin.screens.inventory.InventoryViewModel
import com.pepsigo.admin.screens.location.LocationScreen
import com.pepsigo.admin.screens.location.LocationViewModel
import com.pepsigo.admin.screens.login.LoginScreen
import com.pepsigo.admin.screens.login.LoginViewModel
import com.pepsigo.admin.screens.profile.ProfileScreen
import com.pepsigo.admin.screens.profile.ProfileViewModel
import com.pepsigo.admin.screens.promotions.PromotionalOfferViewModel
import com.pepsigo.admin.screens.promotions.PromotionsScreen
import com.pepsigo.admin.screens.purchase.PurchaseScreen
import com.pepsigo.admin.screens.purchase.PurchaseViewModel
import com.pepsigo.admin.screens.reports.OutstandingDuesScreen
import com.pepsigo.admin.screens.reports.OutstandingDuesViewModel
import com.pepsigo.admin.screens.reports.PurchaseReportScreen
import com.pepsigo.admin.screens.reports.PurchaseReportViewModel
import com.pepsigo.admin.screens.reports.ReportsScreen
import com.pepsigo.admin.screens.reports.SalesReportScreen
import com.pepsigo.admin.screens.reports.SalesReportViewModel
import com.pepsigo.admin.screens.routes.RouteScreen
import com.pepsigo.admin.screens.routes.RouteViewModel
import com.pepsigo.admin.screens.vendors.VendorViewModel
import com.pepsigo.admin.screens.vendors.VendorsScreen
import com.pepsigo.admin.utils.AuthEventBus
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.pepsigo.admin.screens.home.NewHomeScreen
import com.pepsigo.admin.screens.reports.BatchSummaryScreen
import com.pepsigo.admin.screens.reports.BatchSummaryViewModel
import com.pepsigo.admin.screens.reports.ItemWiseDeliveryScreen
import com.pepsigo.admin.screens.reports.ItemWiseSalesScreen
import com.pepsigo.admin.screens.reports.DailyCollectionViewModel
import com.pepsigo.admin.screens.reports.LedgerScreen
import com.pepsigo.admin.screens.reports.LedgerViewModel
import com.pepsigo.admin.screens.reports.PaymentSummaryScreen
import com.pepsigo.admin.screens.reports.PaymentSummaryViewModel
import com.pepsigo.admin.screens.reports.StockSummaryScreen
import com.pepsigo.admin.screens.reports.StockSummaryViewModel
import com.pepsigo.admin.screens.splash.CheckScreen
import com.pepsigo.admin.screens.splash.CheckScreenViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun StartApp(userPreferenceRepository: UserPreferenceRepository) {
    // Entry point for the app's UI
    val navController = rememberNavController()
    val context = LocalContext.current


    // 🔹 Track route here
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val currentRoute = backStackEntry.destination.route
            Log.d("NavigationTracker", "Current route: $currentRoute")
        }
    }

    // listen for unauthorized events
    LaunchedEffect(Unit) {
        AuthEventBus.logoutEvents.collect {
            userPreferenceRepository.clearToken() // suspend, safe here
            Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "checkScreen",

    ){
        composable("checkScreen") { backStackEntry ->
            val checkScreenViewModel: CheckScreenViewModel = viewModel (backStackEntry, factory = CheckScreenViewModel.Factory)
            CheckScreen(checkScreenViewModel,
                onNavigateLogin = {
                    navController.navigate("login") {
                        popUpTo("checkScreen") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("checkScreen") { inclusive = true }
                    }
                }
            )
        }

        composable("login") { backStackEntry ->
            val loginViewModel: LoginViewModel = viewModel(backStackEntry, factory = LoginViewModel.Factory)
            LoginScreen(
                loginViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home",

            ) { backStackEntry ->
//            Log.d("SS1", "Navigated to HomeScreen")
            val logoutViewModel: LogoutViewModel = viewModel(backStackEntry,factory = LogoutViewModel.Factory)
            NewHomeScreen (
                backStackEntry ,
                logoutViewModel,
                onLogoutClicked = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProfileClicked = {
                    navController.navigate("profile"){
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onDrawerItemClicked = {
                    navController.navigate(it.route){
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("profile",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
            ) {
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
            ProfileScreen (
                viewModel = profileViewModel,
                onBackToProfileScreen = {
                    profileViewModel.getProfile()

                },
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )

        }

        composable("location"){
            val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
            LocationScreen(
                viewModel = locationViewModel,
                onBack = { navController.popBackStack("home",inclusive = false) }
            )
        }


        composable("routes") {
            val routeViewModel: RouteViewModel = viewModel(factory = RouteViewModel.Factory)
            RouteScreen(
                viewModel = routeViewModel,
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )
        }

        composable("delivery_executives") {
            val deliveryViewModel: DeliveryExecutiveViewModel = viewModel(factory = DeliveryExecutiveViewModel.Factory)
            DeliveryExecutiveScreen(
                viewModel = deliveryViewModel,
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )

        }

        composable("inventory") {
            val inventoryViewModel: InventoryViewModel = viewModel(factory = InventoryViewModel.Factory)
//            Log.d("SS_inventory", "Navigated to InventoryScreen")
            InventoryScreen(
                viewModel = inventoryViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable("purchase") {
            val purchaseViewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModel.Factory)
//            Log.d("SS_purchase", "Navigated to PurchaseScreen")
            PurchaseScreen(
                viewModel = purchaseViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePurchase = {
                    navController.navigate("create_purchase")
                }
                )

        }

        composable("create_purchase"){
            val createPurchaseViewModel: CreatePurchaseViewModel = viewModel(factory = CreatePurchaseViewModel.Factory)
            CreatePurchaseScreen(
                viewModel = createPurchaseViewModel
            )
        }

        composable("vendors") {
//            Log.d("SS_vendors", "Navigated to VendorsScreen")
            val vendorViewModel: VendorViewModel = viewModel(factory = VendorViewModel.Factory)
            VendorsScreen(
                viewModel = vendorViewModel,
                onPickLocation = { navController.navigate("map") },
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )
        }

        composable("customers") {
             backStackEntry ->
                val customerViewModel: CustomerViewModel =
                    viewModel(factory = CustomerViewModel.Factory)

                val savedStateHandle = backStackEntry.savedStateHandle
                val mapResult = savedStateHandle.getStateFlow<LatLng?>("map_result", null)
                    .collectAsState()

                LaunchedEffect(mapResult.value) {
                    mapResult.value?.let { latLng ->
                        customerViewModel.updateFormCoordinates(latLng.latitude, latLng.longitude)
                        savedStateHandle["map_result"] = null
                    }
                }


            CustomerScreen(
                viewModel = customerViewModel,
                onPickLocation = { navController.navigate("map") },
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )
        }

        composable("map") {
            val context = LocalContext.current
            // 🔹 Initialize Places SDK once
            if (!Places.isInitialized()) {
                // API key from secrets/local.properties (avoid hardcoding)
                Places.initializeWithNewPlacesApiEnabled(context.applicationContext, BuildConfig.MAPS_API_KEY)
            }

            val placesClient = remember { Places.createClient(context) }

            MapScreen(
                placesClient = placesClient,
                onLocationPicked = { lat, lng ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("map_result", LatLng(lat, lng))
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("promotions") {
            val promotionsViewModel: PromotionalOfferViewModel= viewModel(factory = PromotionalOfferViewModel.Factory)
//            Log.d("SS4", "Navigated to PromotionsScreen")
            PromotionsScreen( promotionsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("reports") {
//            Log.d("SS8", "Navigated to FinanceScreen")
            val dailyViewModel: DailyCollectionViewModel = viewModel(factory = DailyCollectionViewModel.Factory)
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) },
                viewModel = dailyViewModel
            )

        }

        composable(Routes.SalesRegister) {
            val salesReportViewModel: SalesReportViewModel = viewModel(factory = SalesReportViewModel.Factory)
            SalesReportScreen(
                viewModel = salesReportViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PurchaseRegister) {
            val purchaseReportViewModel: PurchaseReportViewModel = viewModel(factory = PurchaseReportViewModel.Factory)
            PurchaseReportScreen(
                viewModel = purchaseReportViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.OutstandingDues) {
            val outstandingDuesViewModel: OutstandingDuesViewModel = viewModel(factory = OutstandingDuesViewModel.Factory)
            OutstandingDuesScreen(
                viewModel = outstandingDuesViewModel,
                onNavigateBack = { navController.popBackStack() } )

        }
        composable(Routes.StockSummary) {
            val stockSummaryViewModel: StockSummaryViewModel = viewModel(factory = StockSummaryViewModel.Factory)
            StockSummaryScreen(
                viewModel = stockSummaryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BatchStock) {
            val batchStockViewModel: BatchSummaryViewModel = viewModel(factory = BatchSummaryViewModel.Factory)
            BatchSummaryScreen(
                viewModel = batchStockViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Ledger) {
            val ledgerViewModel: LedgerViewModel = viewModel(factory = LedgerViewModel.Factory)
            LedgerScreen(
                viewModel = ledgerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )

        }

        // New report routes
        composable(Routes.ItemWiseDelivery) {
            ItemWiseDeliveryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.ItemWiseSales) {
            ItemWiseSalesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PaymentSummary) {
            val paymentSummaryViewModel: PaymentSummaryViewModel = viewModel(factory = PaymentSummaryViewModel.Factory)
            PaymentSummaryScreen(
                viewModel = paymentSummaryViewModel,
                onNavigateBack = { navController.popBackStack() })
        }



    }
}
