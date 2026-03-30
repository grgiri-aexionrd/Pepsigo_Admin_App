package com.pepsigo.admin.repository

import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class SalePrintRepoImpl(private val apiService: ApiService) : SalePrintRepo {
    override suspend fun getSalePrintable(saleId: Int): Result<String> {
        return wrapError {
            val response = apiService.getSalePrintable(saleId)
            response.string()
        }
    }
}
