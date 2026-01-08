package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.model.User
import com.pepsigo.admin.repository.PromotionalOfferRepo
import com.pepsigo.admin.repository.UserRepository

data class OfferUi(
    val offerId: Int,
    val invId: Int,
    val itemName: String,
    val itemQuantity: String,
    val itemPrice: String,
    val unit: String,
    val autoAdd: Boolean,
    val canEdit: Boolean
)

class PromotionalOfferUseCase(
    private val userRepository: UserRepository,
    private val promotionalOfferRepository: PromotionalOfferRepo
) {
    suspend fun getCustomers(): Result<List<User>> {
        val result = userRepository.getUsers("customer")
        Log.d("PromotionalOfferUseCase", "UseCaseGetCustomers: $result")
        return result
    }

    suspend fun getPromotionalOffers(customerId: Int): Result<List<OfferUi>> {
        val result =  promotionalOfferRepository.getPromotionalOffers(customerId)
        Log.d("PromotionalOfferUseCase", "UseCaseGetOffers: $result")
         return result
    }
}





