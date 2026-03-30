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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.pepsigo.admin.screens.customer.CustomerUiState
import com.pepsigo.admin.screens.home.NewHomeScreen
import com.pepsigo.admin.screens.makeSales.AddProductScreen
import com.pepsigo.admin.screens.makeSales.BatchSelectionScreen
import com.pepsigo.admin.screens.makeSales.MakeSaleScreen
import com.pepsigo.admin.screens.makeSales.MakeSaleViewModel
import com.pepsigo.admin.screens.makeSales.SaleSuccessScreen
import com.pepsigo.admin.screens.makeSales.SaleSummaryScreen
import com.pepsigo.admin.screens.payment.PaymentScreen
import com.pepsigo.admin.screens.payment.PaymentViewModel
import com.pepsigo.admin.screens.payment.PaymentDetailScreen
import com.pepsigo.admin.screens.payment.MakePaymentScreen
import com.pepsigo.admin.screens.payment.MakePaymentViewModel
import com.pepsigo.admin.screens.printInvoice.PrintInvoiceScreen
import com.pepsigo.admin.screens.printInvoice.PrintInvoiceViewModel
import com.pepsigo.admin.screens.profile.ProfileUiState
import com.pepsigo.admin.screens.promotions.CreatePromotionsScreen
import com.pepsigo.admin.screens.promotions.CreatePromotionsViewModel
import com.pepsigo.admin.screens.reports.BatchSummaryScreen
import com.pepsigo.admin.screens.reports.BatchSummaryViewModel
import com.pepsigo.admin.screens.reports.ItemWiseSalesScreen
import com.pepsigo.admin.screens.reports.DailyCollectionViewModel
import com.pepsigo.admin.screens.reports.DeliveryPerformanceScreen
import com.pepsigo.admin.screens.reports.DeliveryPerformanceViewModel
import com.pepsigo.admin.screens.reports.ItemWiseSalesViewModel
import com.pepsigo.admin.screens.reports.LedgerScreen
import com.pepsigo.admin.screens.reports.LedgerViewModel
import com.pepsigo.admin.screens.reports.PaymentSummaryScreen
import com.pepsigo.admin.screens.reports.PaymentSummaryViewModel
import com.pepsigo.admin.screens.reports.StockSummaryScreen
import com.pepsigo.admin.screens.reports.StockSummaryViewModel
import com.pepsigo.admin.screens.sales.SalesScreen
import com.pepsigo.admin.screens.sales.SalesViewModel
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
                launchSingleTop = true
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
            ) {backStackEntry ->

            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
            val savedStateHandle = backStackEntry.savedStateHandle
            val mapResult = savedStateHandle.getStateFlow<LatLng?>("map_result", null)
                .collectAsState()

            LaunchedEffect(mapResult.value) {
                mapResult.value?.let { latLng ->
                    profileViewModel.updateProfileCoordinates(latLng.latitude, latLng.longitude)
                    savedStateHandle["map_result"] = null
                }
            }

            ProfileScreen (
                viewModel = profileViewModel,
                onBackToProfileScreen = {
                    profileViewModel.getProfile()

                },
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                },
                onLocationUpdate = {
                    val form = profileViewModel.uiState.value as ProfileUiState.EditProfile
                    val latitude = form.profile.latitude.toDoubleOrNull()
                    val longitude = form.profile.longitude.toDoubleOrNull()
                    val latLng = if (latitude != null && longitude != null) LatLng(latitude, longitude) else null

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("initial_location", latLng)

                    navController.navigate("map") }
            )

        }

        composable("sales"){
            val salesViewModel: SalesViewModel = viewModel(factory = SalesViewModel.Factory)
            SalesScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreateSale = {
                    navController.navigate(Routes.MAKE_SALE)
                },
                onNavigateToMakePayment = { saleId, customerId, amount ->
                    navController.navigate(Routes.makePaymentRoute(
                        saleId = saleId,
                        customerId = customerId,
                        amount = amount
                    ))
                },
                onNavigateToPrintInvoice = { saleId ->
                    navController.navigate(Routes.printInvoiceRoute(saleId))
                }
            )
        }

        composable(Routes.MAKE_SALE){ backStackEntry ->
            val makeSaleViewModel: MakeSaleViewModel = viewModel(backStackEntry, factory = MakeSaleViewModel.Factory)
            MakeSaleScreen(
                viewModel = makeSaleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProduct = {
                    navController.navigate(Routes.ADD_PRODUCT)
                },
                onNavigateToSummary = {
                    navController.navigate(Routes.SALE_SUMMARY)
                }
            )
        }

        composable(Routes.ADD_PRODUCT){ backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.MAKE_SALE)
            }
            val makeSaleViewModel: MakeSaleViewModel = viewModel(parentEntry, factory = MakeSaleViewModel.Factory)
            AddProductScreen(
                viewModel = makeSaleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { product ->
                    makeSaleViewModel.onProductClick(product)
                    navController.navigate(Routes.BATCH_SELECTION)
                }
            )
        }

        composable(Routes.BATCH_SELECTION) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.MAKE_SALE)
            }
            val makeSaleViewModel: MakeSaleViewModel = viewModel(parentEntry, factory = MakeSaleViewModel.Factory)
            BatchSelectionScreen(
                viewModel = makeSaleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SALE_SUMMARY) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.MAKE_SALE)
            }
            val makeSaleViewModel: MakeSaleViewModel = viewModel(parentEntry, factory = MakeSaleViewModel.Factory)
            SaleSummaryScreen(
                viewModel = makeSaleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSaleSuccess = {
                    navController.navigate(Routes.SALE_SUCCESS) {
                        // Clear back stack up to MAKE_SALE (exclusive)
                        popUpTo(Routes.MAKE_SALE) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.SALE_SUCCESS) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.MAKE_SALE)
            }
            val makeSaleViewModel: MakeSaleViewModel = viewModel(parentEntry, factory = MakeSaleViewModel.Factory)
            SaleSuccessScreen(
                viewModel = makeSaleViewModel,
                onMakePayment = { saleId, customerId, amount ->
                    navController.navigate(Routes.makePaymentRoute(
                        saleId = saleId,
                        customerId = customerId,
                        amount = amount
                    ))
                },
                onPrintInvoice = {
                    val saleId = makeSaleViewModel.state.value.createdSaleId ?: return@SaleSuccessScreen
                    navController.navigate(Routes.printInvoiceRoute(saleId))
                },
                onNavigateToSales = {
                    // Navigate back to sales screen and clear the entire sale flow
                    navController.popBackStack(Routes.MAKE_SALE,inclusive = true)
                }
            )
        }

        composable(
            Routes.PRINT_INVOICE,
            arguments = listOf(navArgument("saleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getInt("saleId") ?: 0
            val printInvoiceViewModel: PrintInvoiceViewModel = viewModel(
                factory = PrintInvoiceViewModel.provideFactory(saleId)
            )
            PrintInvoiceScreen(
                viewModel = printInvoiceViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPrint = { invoiceText ->
                    // TODO: Implement actual printing
                    // For now, you could use Android's print framework or save to PDF
                }
            )
        }

        composable("payment"){ backStackEntry ->
            val paymentViewModel: PaymentViewModel = viewModel(backStackEntry, factory = PaymentViewModel.Factory)
            PaymentScreen(
                viewModel = paymentViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { payment ->
                    navController.navigate(Routes.paymentDetailRoute(payment.id))
                },
                onNavigateToMakePayment = {
                    navController.navigate(Routes.makePaymentRoute())
                }
            )
        }

//      Make Payment Screen
        composable(
            Routes.MAKE_PAYMENT_ROUTE,
            arguments = listOf(
                navArgument("saleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("purchaseId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("customerId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("amount") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val makePaymentViewModel: MakePaymentViewModel =
                viewModel(
                    backStackEntry,
                    factory = MakePaymentViewModel.Factory
                )

            MakePaymentScreen(
                viewModel = makePaymentViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PaymentDetail,
            arguments = listOf(
                navArgument("paymentId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // Get the parent backstack entry for the payment screen to share the ViewModel
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("payment")
            }
            val paymentViewModel: PaymentViewModel = viewModel(parentEntry, factory = PaymentViewModel.Factory)
            PaymentDetailScreen(
                viewModel = paymentViewModel,
                onNavigateBack = { navController.popBackStack() }
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
            val purchaseViewModel: PurchaseViewModel =
                viewModel(factory = PurchaseViewModel.Factory)
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
                viewModel = createPurchaseViewModel,
                onNavigateBack = { navController.popBackStack() }
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
                onPickLocation = {
                    val form = (customerViewModel.customerUiState.value as CustomerUiState.AddEditCustomer).form

                    val latLng = form.coordinates
                        .takeIf { it.isNotBlank() }
                        ?.split(",")
                        ?.map { it.trim().toDoubleOrNull() }
                        ?.let { list ->
                            if (list.size == 2 && list[0] != null && list[1] != null) {
                                LatLng(list[0]!!, list[1]!!)
                            } else null
                        }

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("initial_location", latLng)
                    navController.navigate("map")
                                 },
                onNavigateBackToHome = {
                    navController.popBackStack("home", false)
                }
            )
        }

        composable("map") {

            val initialLocation =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<LatLng>("initial_location")

            MapScreen(
                initialLocation = initialLocation,
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePromotions = { navController.navigate("create_promotions"){
                    launchSingleTop = true
                    restoreState = true
                } }
            )
        }

        composable("create_promotions") {
            val createPromotionsViewModel: CreatePromotionsViewModel = viewModel(factory = CreatePromotionsViewModel.Factory)
            CreatePromotionsScreen(
                viewModel = createPromotionsViewModel,
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
            val deliveryPerformanceViewModel: DeliveryPerformanceViewModel = viewModel(factory = DeliveryPerformanceViewModel.Factory)
            DeliveryPerformanceScreen(onNavigateBack = { navController.popBackStack() },
                viewModel = deliveryPerformanceViewModel
            )
        }

        composable(Routes.ItemWiseSales) {
            val itemWiseSalesViewModel: ItemWiseSalesViewModel = viewModel(factory = ItemWiseSalesViewModel.Factory)
            ItemWiseSalesScreen(
                viewModel = itemWiseSalesViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PaymentSummary) {
            val paymentSummaryViewModel: PaymentSummaryViewModel = viewModel(factory = PaymentSummaryViewModel.Factory)
            PaymentSummaryScreen(
                viewModel = paymentSummaryViewModel,
                onNavigateBack = { navController.popBackStack() })
        }





    }
}
