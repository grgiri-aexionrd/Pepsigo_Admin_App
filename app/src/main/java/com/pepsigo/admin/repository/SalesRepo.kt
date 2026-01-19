package com.pepsigo.admin.repository

import androidx.paging.PagingData
import com.pepsigo.admin.model.SalesUiModel
import kotlinx.coroutines.flow.Flow

interface SalesRepo {
    suspend fun getSales(): Flow<PagingData<SalesUiModel>>
}