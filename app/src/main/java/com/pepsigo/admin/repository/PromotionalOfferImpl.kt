package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.model.PromotionalOfferCreate
import com.pepsigo.admin.model.PromotionalOfferCreateResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class PromotionalOfferImpl(private val apiService: ApiService): PromotionalOfferRepo {
    override suspend fun getPromotionalOffers(customerId: Int): Result<List<OfferUi>> {
        return wrapError {
            val result = apiService.getPromotionalOffers(customerId)
            result.data?.map{ item -> item.toDomain()} ?: emptyList()
        }
    }

    override suspend fun addOffer(
        customerId: Int,
        inventoryId: Int,
        quantity: Int,
        autoAdd: Boolean,
        salePrice: Double
    ): Result<PromotionalOfferCreateResponse> {
        val body = PromotionalOfferCreate(
            inventoryId = inventoryId,
            quantity = quantity,
            autoAdd = autoAdd,
            salePrice = salePrice
        )
        return wrapError {
            val response = apiService.addPromotionalOffer(customerId, body)
            Log.d("PromotionalOfferImpl", "addOffer: $response")
            response

        }

    }

}