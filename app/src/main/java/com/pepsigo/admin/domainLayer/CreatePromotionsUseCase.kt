package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.PromotionalOfferCreateResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.repository.InventoryRepo
import com.pepsigo.admin.repository.PromotionalOfferRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class CreatePromotionsInitialUi(
    val customer: List<DropDownList> = emptyList(),
    val inventory: List<InventoryListUi> = emptyList(),
    val customerError: AppError? = null,
    val inventoryError: AppError? = null,
)

class CreatePromotionsUseCase (
    private val inventoryRepo: InventoryRepo,
    private val userRepository: UserRepository,
    private val promotionalOfferRepository: PromotionalOfferRepo
){
    suspend fun getCustomersInventory(): CreatePromotionsInitialUi {
        val customerDeferred = coroutineScope {
            async { userRepository.getUsers("customer") }
        }

        val inventoryDeferred = coroutineScope {
            async { inventoryRepo.fetchInventoryItems() }
        }

        val customerResult = customerDeferred.await()
        val inventoryResult = inventoryDeferred.await()

        val customerDropdown = customerResult.map { users ->
            users.map { DropDownList(it.id, it.name) }
        }

        return CreatePromotionsInitialUi(
            customer = customerDropdown.getOrNull() ?: emptyList(),
            inventory = inventoryResult.getOrNull() ?: emptyList(),
            customerError = customerDropdown.exceptionOrNull() as? AppError,
            inventoryError = inventoryResult.exceptionOrNull() as? AppError
        )
    }

    suspend fun addOffer(customerId: Int, inventoryId: Int, quantity: Int, autoAdd:Boolean, salePrice: Double): Result<PromotionalOfferCreateResponse> {
        val response = promotionalOfferRepository.addOffer(
            customerId,
            inventoryId,
            quantity,
            autoAdd,
            salePrice
        )
        Log.d("CreatePromotionsUseCase", "addOffer: $response")
         return response

    }

    }








