package com.pepsigo.admin.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.pepsigo.admin.mapper.toSalesUi
import com.pepsigo.admin.mapper.toUiModel
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesDto
import com.pepsigo.admin.model.SalesResponse
import com.pepsigo.admin.model.SalesReturnItems
import com.pepsigo.admin.model.SalesReturnRequest
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.screens.sales.SalesReturnItemList
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SalesRepoImpl(private val apiService: ApiService) : SalesRepo{
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getSales(): Flow<PagingData<SalesUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SalesPagingSource(apiService) }
        ).flow
            .map { pagingData ->
                pagingData.map { salesDto ->
                    salesDto.toUiModel()
                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getSalesById(id: Int): Result<SalesDetailUi> {
        return wrapError{
            val response = apiService.getSaleById(id)
            // If server returned null data unexpectedly
            val data = response.data ?: throw AppError.Server(
                500,
                response.details ?: "Invalid response: data is null"
            )
            data.toSalesUi()
        }
    }

    override suspend fun cancelSale(id: Int): Result<SalesResponse<Unit>> {
        return wrapError {
            val response = apiService.cancelSale(id)
            response
        }
    }

    override suspend fun returnSale(
        returnItem: List<SalesReturnItemList>,
        id: Int
    ): Result<SalesResponse<SalesReturnResponse>> {
        val apiItems = returnItem.map { it.toApi() }
        val payload = SalesReturnRequest(items = apiItems)
        return wrapError {
            val response = apiService.returnSale(id, payload)
            response
        }
    }
}

// Extension function to convert SalesReturnItemList to SalesReturnItems (API model)
private fun SalesReturnItemList.toApi(): SalesReturnItems {
    return SalesReturnItems(
        invId = this.invId,
        quantity = this.quantity.toIntOrNull() ?: 1
    )
}

class SalesPagingSource(private val apiService: ApiService) : PagingSource<Int, SalesDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SalesDto>{
        val page = params.key ?: 1
        val result = wrapError {
            apiService.getSales(page)
        }
        return result.fold(
            onSuccess = { response ->
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.nextPageUrl == null) null else page + 1
                )
            },
            onFailure = {error ->
                LoadResult.Error(
                    when(error) {
                        is AppError -> error                // << Here
                        else -> error
                    }
                )
            }

        )
    }

    override fun getRefreshKey(state: PagingState<Int, SalesDto>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

}
