package com.pepsigo.admin.repository

interface SalePrintRepo {
    suspend fun getSalePrintable(saleId: Int): Result<String>
}
