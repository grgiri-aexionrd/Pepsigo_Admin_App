package com.pepsigo.admin.fakeTesting

import android.content.Context
import com.pepsigo.admin.AppContainer
import com.pepsigo.admin.repository.DeliveryExecutiveStatusRepo
import com.pepsigo.admin.repository.LocationRepository
import com.pepsigo.admin.repository.SalesPurchaseReportRepository
import com.pepsigo.admin.repository.UserRepository

class FakeAppContainer(context: Context) : AppContainer(context) {
    override val userRepository: UserRepository by lazy {
        FakeUserRepository()
    }

    override val locationRepository: LocationRepository by lazy {
        FakeLocationRepository()
    }

    override val deliveryRepository: DeliveryExecutiveStatusRepo by lazy {
        FakeDeliveryExecutiveStatusRepo()
    }

    override val salesPurchaseReportRepository: SalesPurchaseReportRepository by lazy {
        FakeSalesPurchaseReportRepository()
    }

}