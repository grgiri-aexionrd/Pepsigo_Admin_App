package com.pepsigo.admin.repository

import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.model.PromotionalOfferCreateResponse

interface PromotionalOfferRepo {
    suspend fun getPromotionalOffers(customerId: Int): Result<List<OfferUi>>
    suspend fun addOffer(customerId: Int, inventoryId: Int, quantity: Int, autoAdd:Boolean, salePrice: Double): Result<PromotionalOfferCreateResponse>

}