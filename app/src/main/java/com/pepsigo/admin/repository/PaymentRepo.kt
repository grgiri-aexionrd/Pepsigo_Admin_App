package com.pepsigo.admin.repository

import androidx.paging.PagingData
import com.pepsigo.admin.domainLayer.MakePaymentRequest
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.model.PaymentUpdateUiModel
import com.pepsigo.admin.model.UpdatePaymentRequest
import kotlinx.coroutines.flow.Flow

interface PaymentRepo {
    suspend fun getPayments(
        transactionType: String? = null,
        customerId: Int? = null,
        date: String? = null
    ): Flow<PagingData<PaymentUiModel>>

    suspend fun getPaymentById(id: Int): Result<PaymentUiModel>

    suspend fun createPayment(request: MakePaymentRequest): Result<PaymentUiResult<PaymentUpdateUiModel>>

    suspend fun updatePayment(id: Int, request: UpdatePaymentRequest): Result<PaymentUiResult<PaymentUpdateUiModel>>

    suspend fun cancelPayment(id: Int): Result<Unit>
}
