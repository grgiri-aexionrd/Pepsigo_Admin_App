package com.pepsigo.admin.domainLayer

import android.util.Log
import com.pepsigo.admin.model.CreatePurchaseRequest
import com.pepsigo.admin.model.PurchaseItem
import com.pepsigo.admin.model.PurchaseResponse
import com.pepsigo.admin.model.PurchaseReturnResponse
import com.pepsigo.admin.repository.InventoryRepo
import com.pepsigo.admin.repository.PurchaseRepo
import com.pepsigo.admin.repository.UserRepository
import com.pepsigo.admin.screens.createPurchaseScreen.ProductList
import com.pepsigo.admin.screens.createPurchaseScreen.PurchaseItemsUi
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.collections.emptyList


data class CreatePurchaseInitialData(
    val vendors: List<DropDownList> = emptyList(),
    val inventory: List<ProductList> = emptyList(),
    val vendorError: AppError? = null,
    val inventoryError: AppError? = null
)

class CreatePurchaseUseCase(
    private val userRepository: UserRepository,
    private val inventoryRepo: InventoryRepo,
    private val purchaseRepo: PurchaseRepo
) {
    suspend fun getVendorsInventory(role: String = "vendor"): CreatePurchaseInitialData {

        // Run both calls concurrently for performance
        val vendorDeferred = coroutineScope {
            async { userRepository.getUsers(role) }
        }

        val inventoryDeferred = coroutineScope {
            async { inventoryRepo.fetchInventoryItems() }
        }

        val vendorResult = vendorDeferred.await()
        val inventoryResult = inventoryDeferred.await()

        Log.d("CreatePurchaseUseCase", "Fetched users for role $role: $vendorResult")
        Log.d("CreatePurchaseUseCase", "Fetched inventory: $inventoryResult")

        val vendorDropDown = vendorResult.map { users ->
            users.map { DropDownList(it.id, it.name) }
        }
        Log.d("CreatePurchaseUseCase", "Mapped vendors: $vendorDropDown")

        val inventoryDropDown = inventoryResult.map { inventory ->
            inventory.map { ProductList(it.id, it.name+"("+it.unit+")", it.unit, it.gstPercent.toString()) }
        }
        Log.d("CreatePurchaseUseCase", "Mapped inventory: $inventoryDropDown")


        return  CreatePurchaseInitialData(
            vendors = vendorDropDown.getOrNull() ?: emptyList(),
            inventory = inventoryDropDown.getOrNull() ?: emptyList(),
            vendorError = vendorDropDown.exceptionOrNull() as? AppError,
            inventoryError = inventoryDropDown.exceptionOrNull() as? AppError
        )
    }

    suspend fun submitPurchase(
        vendorId: Int,
        invoiceNumber: String,
        purchaseDate: String,
        addEditItemDetails: List<PurchaseItemsUi>
    ) : Result<PurchaseResponse<PurchaseReturnResponse>>{

        val finalItems = CreatePurchaseRequest(
            vendorId = vendorId,
            invoiceNumber = invoiceNumber.ifBlank { null },
            purchaseDate = purchaseDate,
            items = addEditItemDetails.map { item ->
                PurchaseItem(
                    inventoryId = item.id,
                    itemQuantity = item.itemQuantity.toInt(),
                    unit = item.unit,
                    gstPercent = item.gstPercent.toDouble(),
                    costPrice = item.costPrice.toDouble(),
                    salePrice = item.salePrice.toDouble(),
                    retailPrice = item.retailPrice.toDouble(),
                    expiryDate = item.expiryDate
                )
            }

        )

        Log.d("CreatePurchaseUseCase", "submitPurchaseUseCase: $finalItems")

        return purchaseRepo.createPurchase(finalItems)


    }



}



