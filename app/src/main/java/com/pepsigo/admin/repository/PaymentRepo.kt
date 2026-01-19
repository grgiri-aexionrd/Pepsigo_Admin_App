package com.pepsigo.admin.repository

import androidx.paging.PagingData
import com.pepsigo.admin.model.PaymentDto
import kotlinx.coroutines.flow.Flow

interface PaymentRepo {
    suspend fun getPayments(
        transactionType: String? = null,
        customerId: Int? = null,
        date: String? = null
    ): Flow<PagingData<PaymentDto>>
}
