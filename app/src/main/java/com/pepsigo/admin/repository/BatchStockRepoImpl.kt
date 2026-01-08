package com.pepsigo.admin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.BatchStockData
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class BatchStockRepoImpl(private val apiService: ApiService) : BatchStockRepo {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getBatchStock(id:Int?): Result<BatchStockData> {
        return wrapError {
            val result = apiService.batchStock(id)
            Log.d("BatchStockRepoImpl", "getBatchStock result: $result")
            result.toDomain()
        }
    }
}