package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.model.DeliveryPerformanceResponse
import com.pepsigo.admin.model.PaymentSummaryResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class DailyCollectionRepoImpl(private val apiService: ApiService): DailyCollectionRepo {
    override suspend fun getDailyCollection(date: String): Result<DailyCollectionResponse>{
        return wrapError {
           val response =  apiService.dailyCollection(date)
            response
        }
    }

    override suspend fun getPaymentSummary(
        from: String,
        to: String
    ): Result<PaymentSummaryResponse> {
        return wrapError {
            val response = apiService.paymentSummary(from, to)
            Log.d("Payment Summary", "getPaymentSummary: $response")
            response
        }
    }

    override suspend fun getDeliveryPerformance( from: String,to: String): Result<DeliveryPerformanceResponse> {
        return wrapError {
            val response = apiService.deliveryPerformance(from, to)
            Log.d("Delivery Performance", "getDeliveryPerformance: $response")
            response
        }
    }

}
