package com.pepsigo.admin.repository

import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class PromotionalOfferImpl(private val apiService: ApiService): PromotionalOfferRepo {
    override suspend fun getPromotionalOffers(customerId: Int): Result<List<OfferUi>> {
        return wrapError {
            val result = apiService.getPromotionalOffers(customerId)
            result.data?.map{ item -> item.toDomain()} ?: emptyList()
        }
    }

}