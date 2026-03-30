package com.pepsigo.admin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.pepsigo.admin.domainLayer.MakePaymentRequest
import com.pepsigo.admin.mapper.toUiModel
import com.pepsigo.admin.model.DenominationRequest
import com.pepsigo.admin.model.MakePaymentRequestDto
import com.pepsigo.admin.model.PaymentDto
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.model.PaymentUpdateUiModel
import com.pepsigo.admin.model.UpdatePaymentRequest
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class PaymentUiResult<T>(
    val message: String?,
    val data: T?
)

class PaymentRepoImpl(private val apiService: ApiService) : PaymentRepo {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPayments(
        transactionType: String?,
        customerId: Int?,
        date: String?
    ): Flow<PagingData<PaymentUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PaymentsPagingSource(
                    apiService = apiService,
                    transactionType = transactionType,
                    customerId = customerId,
                    date = date
                )
            }
        ).flow
            .map { pagingData ->
                pagingData.map { paymentDto ->
                    paymentDto.toUiModel()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPaymentById(id: Int): Result<PaymentUiModel> {
        return wrapError {
            val result = apiService.getPaymentById(id)
            // If server returned null data unexpectedly
            val data = result.data ?: throw AppError.Server(
                500,
                result.details ?: "Invalid response: data is null"
            )

            Log.d("PaymentRepoImpl", "Mapped UI Data: $data")
            data.toUiModel()
        }
    }

    override suspend fun createPayment(request: MakePaymentRequest): Result<PaymentUiResult<PaymentUpdateUiModel>> {
        return wrapError {
            val paymentDenomination = MakePaymentRequestDto(
                saleId = request.saleId,
                purchaseId = request.purchaseId,
                customerId = request.customerId!!,
                amount = request.amount,
                paymentMethod = request.paymentMethod,
                transactionType = request.transactionType,
                refNumber = request.refNumber,
                denomination = request.denomination?.let {
                    DenominationRequest(
                        denom2000 = it.denom2000,
                        denom500 = it.denom500,
                        denom200 = it.denom200,
                        denom100 = it.denom100,
                        denom50 = it.denom50,
                        denom20 = it.denom20,
                        denom10 = it.denom10,
                        denom5 = it.denom5,
                        denom2 = it.denom2,
                        denom1 = it.denom1,
                        card = it.card,
                        upi = it.upi,
                        netBanking = it.netBanking,
                        cheque = it.cheque,
                        credit = it.credit
                    )
                }
            )
            val result = apiService.createPayment(paymentDenomination)
            val data = result.data ?: throw AppError.Server(
                500,
                result.details ?: "Invalid response: data is null"
            )
            Log.d("PaymentRepoImpl", "Created Payment: $data")
            PaymentUiResult(
                message = result.message,
                data = data.toUiModel()
            )

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updatePayment(id: Int, request: UpdatePaymentRequest): Result<PaymentUiResult<PaymentUpdateUiModel>>{
        return wrapError {
            val result = apiService.updatePayment(id, request)
            val data = result.data ?: throw AppError.Server(
                500,
                result.details ?: "Invalid response: data is null"
            )
            Log.d("PaymentRepoImpl", "Updated Payment: $data")

           PaymentUiResult(
               message = result.message,
               data = data.toUiModel()
           )

        }
    }

    override suspend fun cancelPayment(id: Int): Result<Unit> {
        return wrapError {
            apiService.cancelPayment(id)
            Unit
        }
    }
}

class PaymentsPagingSource(
    private val apiService: ApiService,
    private val transactionType: String?,
    private val customerId: Int?,
    private val date: String?
) : PagingSource<Int, PaymentDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaymentDto> {
        val page = params.key ?: 1
        val result = wrapError {
            apiService.getPayments(
                page = page,
                transactionType = transactionType,
                customerId = customerId,
                date = date
            )
        }
        return result.fold(
            onSuccess = { response ->
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.nextPageUrl == null) null else page + 1
                )
            },
            onFailure = { error ->
                LoadResult.Error(
                    when (error) {
                        is AppError -> error
                        else -> error
                    }
                )
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PaymentDto>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}


