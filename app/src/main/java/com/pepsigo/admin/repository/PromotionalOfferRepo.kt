package com.pepsigo.admin.repository

import com.pepsigo.admin.domainLayer.OfferUi

interface PromotionalOfferRepo {
    suspend fun getPromotionalOffers(customerId: Int): Result<List<OfferUi>>
}