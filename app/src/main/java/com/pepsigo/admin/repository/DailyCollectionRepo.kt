package com.pepsigo.admin.repository

import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.model.PaymentSummaryResponse


interface DailyCollectionRepo {
    suspend fun getDailyCollection(date: String): Result<DailyCollectionResponse>
    suspend fun getPaymentSummary(from: String, to: String): Result<PaymentSummaryResponse>
}

