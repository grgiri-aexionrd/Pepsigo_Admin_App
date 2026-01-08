package com.pepsigo.admin.network


import com.pepsigo.admin.model.AddDeliveryExecDto
import com.pepsigo.admin.model.AddInventoryRequest
import com.pepsigo.admin.model.AddUserRequest
import com.pepsigo.admin.model.BatchStockResponse
import com.pepsigo.admin.model.CheckLoginResponse
import com.pepsigo.admin.model.CreatePurchaseRequest
import com.pepsigo.admin.model.CustomerLedgerResponse
import com.pepsigo.admin.model.DashboardDto
import com.pepsigo.admin.model.EditInventoryRequest
import com.pepsigo.admin.model.ProfileRequest
import com.pepsigo.admin.model.LocationResponse
import com.pepsigo.admin.model.LoginRequest
import com.pepsigo.admin.model.LoginResponse
import com.pepsigo.admin.model.EmailUpdateRequest
import com.pepsigo.admin.model.ExecutiveStatusResponse
import com.pepsigo.admin.model.FCMTokenUpdateRequest
import com.pepsigo.admin.model.FCMTokenUpdateResponse
import com.pepsigo.admin.model.GetInventoryResponse
import com.pepsigo.admin.model.GetRoutesDto
import com.pepsigo.admin.model.InventoryItem
import com.pepsigo.admin.model.InventoryItemDetailDto
import com.pepsigo.admin.model.InventoryResponse
import com.pepsigo.admin.model.LocationDto
import com.pepsigo.admin.model.OutstandingPayableResponse
import com.pepsigo.admin.model.OutstandingReceivablesResponse
import com.pepsigo.admin.model.PasswordUpdateRequest
import com.pepsigo.admin.model.ProfileEmailPasswordUpdateResponse
import com.pepsigo.admin.model.ProfileUpdateRequest
import com.pepsigo.admin.model.PromotionalOfferDto
import com.pepsigo.admin.model.PromotionalOfferResponse
import com.pepsigo.admin.model.PurchaseDetailDto
import com.pepsigo.admin.model.PurchasePaginatedResponseDto
import com.pepsigo.admin.model.PurchaseRegisterResponse
import com.pepsigo.admin.model.PurchaseResponse
import com.pepsigo.admin.model.PurchaseReturnRequest
import com.pepsigo.admin.model.PurchaseReturnResponse
import com.pepsigo.admin.model.RouteAddEditRequest
import com.pepsigo.admin.model.RouteAssignRequest
import com.pepsigo.admin.model.RouteAssignResponse
import com.pepsigo.admin.model.RouteResponse
import com.pepsigo.admin.model.SalesRegisterResponse
import com.pepsigo.admin.model.StockSummaryResponse
import com.pepsigo.admin.model.UserDto
import com.pepsigo.admin.model.UserResponse
import com.pepsigo.admin.model.VendorLedgerResponse
import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.model.PaymentSummaryResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // login endpoint
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // FCMToken update
    @POST("fcm-token")
    suspend fun updateFcmToken(@Body request: FCMTokenUpdateRequest): FCMTokenUpdateResponse


    // check login status endpoint
    @GET("check-login")
    suspend fun checkLogin(): CheckLoginResponse

    //Dashboard endpoint
    @GET("dashboard")
    suspend fun getDashboardData(): DashboardDto // Replace Any with actual Dashboard data

    // Get Profile endpoint
    @GET("profile")
    suspend fun getProfile(): ProfileRequest

    @PUT("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): ProfileEmailPasswordUpdateResponse

    //Update Email
    @PUT("profile/email")
    suspend fun updateEmail(@Body request: EmailUpdateRequest): ProfileEmailPasswordUpdateResponse

    @PUT("profile/password")
    suspend fun updatePassword(@Body request: PasswordUpdateRequest): ProfileEmailPasswordUpdateResponse

    // Get users (customers,delivery,vendors)  endpoints
    @GET("users")
    suspend fun getUsers(@Query("role") role: String): List<UserDto> // Replace Any with actual Customer data class

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    @POST("users/customer")
    suspend fun addCustomer(@Body request: AddUserRequest): UserResponse

    @POST("users/vendor")
    suspend fun addVendor(@Body request: AddUserRequest): UserResponse

    @POST("users/delivery-executive")
    suspend fun addDeliveryExecutive(@Body request: AddDeliveryExecDto): UserResponse

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: AddUserRequest
    ): UserResponse

    @PATCH("users/{id}/toggle")
    suspend fun toggleUser(@Path("id") id: Int): UserResponse

    // Delivery executives status endpoint
    @GET("delivery-executives/status")
    suspend fun getDeliveryExecutivesStatus(): ExecutiveStatusResponse // Replace Any with actual ExecutiveStatus data class

//    Locations endpoints
    @GET("locations")
    suspend fun getLocations(): LocationResponse<List<LocationDto>>


    @GET("locations/{id}")
    suspend fun getLocationById(@Path("id") id: Int): LocationDto

    @POST("locations")
    suspend fun addLocation(@Body body: Map<String, String>): LocationResponse<LocationDto>

    @PUT("locations/{id}")
    suspend fun updateLocation(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): LocationResponse<LocationDto>

    @PATCH("locations/{id}/status")
    suspend fun toggleStatus(@Path("id") id: Int): LocationResponse<Unit>



    // Routes endpoints
    @GET("routes")
    suspend fun getRoutes(): RouteResponse<List<GetRoutesDto>> // Replace Any with actual Route data class

    @POST("route-assignments")
    suspend fun assignDeliveryExecutiveToRoute(
        @Body request: RouteAssignRequest
    ): RouteResponse<RouteAssignResponse>


    @POST("routes")
    suspend fun createRoute(
        @Body request: RouteAddEditRequest
    ): RouteResponse<LocationDto>

    @PUT("routes/{id}")
    suspend fun updateRoute(
        @Path("id") id: Int,
        @Body request: RouteAddEditRequest
    ): RouteResponse<LocationDto>

    @PATCH("routes/{id}/status")
    suspend fun toggleRoute(@Path("id") id: Int ): RouteResponse<LocationDto>




    // Inventory endpoints
    @GET("inventory")
    suspend fun getInventory(): GetInventoryResponse

    @GET("inventory/{id}")
    suspend fun getInventoryById(@Path("id") id: Int): InventoryResponse<InventoryItemDetailDto>

    @POST("inventory")
    suspend fun addInventory(@Body body: AddInventoryRequest): InventoryResponse<InventoryItem>


    @PUT("inventory/{id}")
    suspend fun updateInventory(
        @Path("id") id: Int,
        @Body body: EditInventoryRequest
    ): InventoryResponse<InventoryItem>

    @PATCH("inventory/{id}/status")
    suspend fun toggleInventoryStatus(@Path("id") id: Int): InventoryResponse<Any>

    // Purchase endpoints
    @GET("purchases")
    suspend fun getPurchases(@Query("page") page: Int): PurchasePaginatedResponseDto

    @GET("purchases/{id}")
    suspend fun getPurchaseById(@Path("id") id: Int): PurchaseResponse<PurchaseDetailDto>

    @POST("purchases")
    suspend fun createPurchase(@Body body: CreatePurchaseRequest ): PurchaseResponse<PurchaseReturnResponse>


    @PATCH("purchases/{id}/cancel")
    suspend fun cancelPurchase(@Path("id") id: Int): PurchaseResponse<Unit>

    @POST("purchases/{id}/return")
    suspend fun returnPurchase(
        @Path("id") id: Int,
        @Body body: PurchaseReturnRequest
    ): PurchaseResponse<PurchaseReturnResponse>

    // Promotional Offer
    @GET("customers/{id}/free-products")
    suspend fun getPromotionalOffers(@Path("id") id: Int): PromotionalOfferResponse<List<PromotionalOfferDto>>


    // Reports endpoints
    @GET("sales-register")
    suspend fun getSalesRegister(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("customer_id") customerId: Int? = null
    ): SalesRegisterResponse

    @GET("purchase-register")
    suspend fun getPurchaseRegister(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("vendor_id") vendorId: Int? = null
    ): PurchaseRegisterResponse

    @GET("outstanding-receivables")
    suspend fun outstandingReceivables(
        @Query("customer_id") customerId: Int? = null
    ): OutstandingReceivablesResponse

    @GET("outstanding-payables")
    suspend fun outstandingPayables(
        @Query("vendor_id") vendorId: Int? = null
    ): OutstandingPayableResponse

    @GET("stock-summary")
    suspend fun getStockSummary(): StockSummaryResponse

    @GET("batch-stock")
    suspend fun batchStock(
        @Query("inventory_id") inventoryId: Int? = null
    ): BatchStockResponse

    @GET("customer-ledger/{customer_id}")
    suspend fun customerLedger(
        @Path("customer_id") customerId: Int,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): CustomerLedgerResponse

    @GET("vendor-ledger/{vendor_id}")
    suspend fun vendorLedger(
        @Path("vendor_id") vendorId: Int,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): VendorLedgerResponse

    // daily collection endpoint
    @GET("daily-collection")
    suspend fun dailyCollection(@Query("date") date: String): DailyCollectionResponse

    // Payment Summary
    @GET("payment-summary")
    suspend fun paymentSummary(
        @Query("from") from: String,
        @Query("to") to: String
    ): PaymentSummaryResponse


}