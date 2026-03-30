package com.pepsigo.admin

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pepsigo.admin.network.ApiService
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pepsigo.admin.domainLayer.DeliveryExecutiveUseCase
import com.pepsigo.admin.repository.LocationRepositoryImpl
import com.pepsigo.admin.data.TokenProvider
import com.pepsigo.admin.data.UserPreferenceRepository
import com.pepsigo.admin.domainLayer.BatchStockUseCase
import com.pepsigo.admin.domainLayer.CreatePromotionsUseCase
import com.pepsigo.admin.domainLayer.CreatePurchaseUseCase
import com.pepsigo.admin.domainLayer.OutstandingDuesUseCase
import com.pepsigo.admin.domainLayer.PaymentUseCase
import com.pepsigo.admin.domainLayer.PromotionalOfferUseCase
import com.pepsigo.admin.domainLayer.RouteUseCase
import com.pepsigo.admin.domainLayer.SalesPurchaseReportUseCase
import com.pepsigo.admin.network.AuthInterceptor
import com.pepsigo.admin.network.TokenInterceptor
import com.pepsigo.admin.repository.AuthRepository
import com.pepsigo.admin.repository.BatchStockRepo
import com.pepsigo.admin.repository.BatchStockRepoImpl
import com.pepsigo.admin.repository.DailyCollectionRepo
import com.pepsigo.admin.repository.DailyCollectionRepoImpl
import com.pepsigo.admin.repository.DashboardRepo
import com.pepsigo.admin.repository.DashboardRepoImpl
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepo
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepoImpl
import com.pepsigo.admin.repository.InventoryRepo
import com.pepsigo.admin.repository.InventoryRepoImpl
import com.pepsigo.admin.repository.LedgerRepo
import com.pepsigo.admin.repository.LedgerRepoImpl
import com.pepsigo.admin.repository.LocationRepository
import com.pepsigo.admin.repository.OutstandingDuesRepo
import com.pepsigo.admin.repository.OutstandingDuesRepoImpl
import com.pepsigo.admin.repository.PaymentRepo
import com.pepsigo.admin.repository.PaymentRepoImpl
import com.pepsigo.admin.repository.ProfileRepository
import com.pepsigo.admin.repository.PromotionalOfferImpl
import com.pepsigo.admin.repository.PromotionalOfferRepo
import com.pepsigo.admin.repository.RouteRepository
import com.pepsigo.admin.repository.SalesPurchaseReportRepository
import com.pepsigo.admin.repository.SalesPurchaseReportRepositoryImpl
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.repository.UserRepositoryImpl
import com.pepsigo.admin.repository.PurchaseRepo
import com.pepsigo.admin.repository.PurchaseRepoImpl
import com.pepsigo.admin.repository.RouteRepoImpl
import com.pepsigo.admin.repository.SalesRepo
import com.pepsigo.admin.repository.SalesRepoImpl
import com.pepsigo.admin.repository.StockSummaryRepo
import com.pepsigo.admin.repository.StockSummaryRepoImpl
import com.pepsigo.admin.repository.MakeSalesRepo
import com.pepsigo.admin.repository.MakeSalesRepoImpl
import com.pepsigo.admin.repository.SalePrintRepo
import com.pepsigo.admin.repository.SalePrintRepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue



private const val TOKEN_PREFERENCE_NAME = "user_preferences"
 val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_PREFERENCE_NAME)
open class AppContainer(context: Context) {

    // ✅ Application-wide coroutine scope
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var userPreferenceRepository: UserPreferenceRepository = UserPreferenceRepository(context.dataStore)

    val tokenProvider: TokenProvider by lazy {
        TokenProvider(userPreferenceRepository,appScope)
    }
    private val tokenInterceptor: TokenInterceptor by lazy {
        TokenInterceptor(tokenProvider)
    }
    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(tokenProvider)
    }


    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pepsigo.app/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, userPreferenceRepository)
    }

    open val dashboardRepository: DashboardRepo by lazy {
        DashboardRepoImpl(apiService)
    }
    open val locationRepository : LocationRepository by lazy{
        LocationRepositoryImpl(apiService)
    }

    // Routes dependency injection
    open val routeRepository: RouteRepository by lazy {
        RouteRepoImpl(apiService)
    }

    // RouteUseCase dependency injection
    open val routeUseCase: RouteUseCase by lazy {
        RouteUseCase(routeRepository, locationRepository, deliveryRepository)
    }


    // Profile dependency injection
    open val profileRepository: ProfileRepository by lazy {
        ProfileRepository(apiService)
    }


    open val userRepository : UserRepository by lazy {
        UserRepositoryImpl(apiService)
    }

    open val deliveryRepository : DeliveryExecutiveStatusRepo by lazy {
        DeliveryExecutiveStatusRepoImpl(apiService)
    }

    open val deliveryUseCase: DeliveryExecutiveUseCase by lazy{
        DeliveryExecutiveUseCase(
            userRepository,
            deliveryRepository
        )
    }
     open val salesPurchaseReportRepository : SalesPurchaseReportRepository by lazy {
        SalesPurchaseReportRepositoryImpl(apiService)
    }

     val salesPurchaseReportUseCase: SalesPurchaseReportUseCase by lazy {
        SalesPurchaseReportUseCase(
            userRepository,
            salesPurchaseReportRepository

        )
    }

    open val duesRepo: OutstandingDuesRepo by lazy{
        OutstandingDuesRepoImpl(apiService)
    }

    val duesUseCase: OutstandingDuesUseCase by lazy {
        OutstandingDuesUseCase(userRepository, duesRepo)
    }

    open val inventoryRepo : InventoryRepo by lazy {
        InventoryRepoImpl(apiService)
    }

    open val purchaseRepo : PurchaseRepo by lazy {
        PurchaseRepoImpl(apiService)
    }

    val salesRepo: SalesRepo by lazy {
        SalesRepoImpl(apiService)
    }

    open val promoOfferRepo : PromotionalOfferRepo by lazy {
        PromotionalOfferImpl(apiService)
    }


    open val createPurchaseUseCase: CreatePurchaseUseCase by lazy {
        CreatePurchaseUseCase(
            userRepository,
            inventoryRepo,
            purchaseRepo

        )
    }

    open val promotionalOfferUseCase: PromotionalOfferUseCase by lazy {
        PromotionalOfferUseCase(
            userRepository,
            promoOfferRepo
        )
    }

    open val createPromotionsUseCase: CreatePromotionsUseCase by lazy {
            CreatePromotionsUseCase(inventoryRepo, userRepository,promoOfferRepo)
    }

    val stockSummaryRepo : StockSummaryRepo by lazy {
        StockSummaryRepoImpl(apiService)
    }

    open val batchStockRepo: BatchStockRepo by lazy {
        BatchStockRepoImpl(apiService)
    }

    open val batchStockUseCase: BatchStockUseCase by lazy {
        BatchStockUseCase(
            batchStockRepo,
            inventoryRepo
        )
    }

    open val ledgerRepo : LedgerRepo by lazy {
        LedgerRepoImpl(apiService)
    }

    open val dailyCollectionRepo: DailyCollectionRepo by lazy {
        DailyCollectionRepoImpl(apiService)
    }

    open val paymentRepo: PaymentRepo by lazy {
        PaymentRepoImpl(apiService)
    }

    open val makeSalesRepo: MakeSalesRepo by lazy {
        MakeSalesRepoImpl(apiService)
    }

    open val salePrintRepo: SalePrintRepo by lazy {
        SalePrintRepoImpl(apiService)
    }

    open val paymentUseCase: PaymentUseCase by lazy {
        PaymentUseCase(paymentRepo, userRepository)
    }
 }
