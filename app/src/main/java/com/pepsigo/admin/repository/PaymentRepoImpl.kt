package com.pepsigo.admin.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pepsigo.admin.model.PaymentDto
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow

class PaymentRepoImpl(private val apiService: ApiService) : PaymentRepo {

    override suspend fun getPayments(
        transactionType: String?,
        customerId: Int?,
        date: String?
    ): Flow<PagingData<PaymentDto>> {
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
