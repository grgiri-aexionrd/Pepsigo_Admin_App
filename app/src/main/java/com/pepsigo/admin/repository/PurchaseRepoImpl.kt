package com.pepsigo.admin.repository


import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.pepsigo.admin.mapper.toApi
import com.pepsigo.admin.mapper.toUi
import com.pepsigo.admin.mapper.toUiModel
import com.pepsigo.admin.model.CreatePurchaseRequest
import com.pepsigo.admin.model.PurchaseDetailUi
import com.pepsigo.admin.model.PurchaseDto
import com.pepsigo.admin.model.PurchaseResponse
import com.pepsigo.admin.model.PurchaseReturnRequest
import com.pepsigo.admin.model.PurchaseReturnResponse
import com.pepsigo.admin.model.PurchaseUiModel
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.screens.purchase.ReturnItemList
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PurchaseRepoImpl(private val apiService: ApiService): PurchaseRepo {
    override suspend fun getPurchases(): Flow<PagingData<PurchaseUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PurchasePagingSource(apiService)
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map { purchaseDto ->
                    purchaseDto.toUiModel()
                }
            }
    }

    override suspend fun getPurchaseById(id: Int): Result<PurchaseDetailUi> {
        return wrapError {
            Log.d("PurchaseRepoImpl", "getPurchaseById: $id")
            val response = apiService.getPurchaseById(id)
            Log.d("PurchaseRepoImpl", "getPurchaseById: $response")

            // If server returned null data unexpectedly
            val data = response.data ?: throw AppError.Server(
                500,
                response.details ?: "Invalid response: data is null"
            )

            Log.d("PurchaseRepoImpl", "Mapped UI Data: $data")

            // map data + hasSales to UI
            data.toUi(hasSales = response.hasSales ?: false)
        }
    }

    override suspend fun createPurchase(finalItems: CreatePurchaseRequest): Result<PurchaseResponse<PurchaseReturnResponse>> {
        return wrapError {
            Log.d("PurchaseRepoImpl", "createPurchase: $finalItems")
            val response = apiService.createPurchase(finalItems)
            response
        }
    }

    override suspend fun cancelPurchase(id: Int): Result<PurchaseResponse<Unit>> {
        return wrapError {
            Log.d("PurchaseRepoImpl", "cancelPurchase: $id")
            val response = apiService.cancelPurchase(id)
            Log.d("PurchaseRepoImpl", "cancelPurchase response: $response")
            response
        }
    }

    override suspend fun returnPurchase(
        returnItem: List<ReturnItemList>,
        id: Int
    ): Result<PurchaseResponse<PurchaseReturnResponse>> {
        val apiItems = returnItem.map { it.toApi() }
        val payload = PurchaseReturnRequest(
            items = apiItems
        )
        return wrapError {
            val response = apiService.returnPurchase(id,payload )
            response
        }
    }

}



class PurchasePagingSource(private val apiService: ApiService): PagingSource<Int, PurchaseDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PurchaseDto> {
        val page = params.key ?: 1
        val result = wrapError {
            apiService.getPurchases(page)
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

    override fun getRefreshKey(state: PagingState<Int, PurchaseDto>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }


}