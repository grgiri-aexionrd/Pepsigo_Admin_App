package com.pepsigo.admin.domainLayer

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.BatchStockData
import com.pepsigo.admin.model.BatchStockDetail
import com.pepsigo.admin.repository.BatchStockRepo
import com.pepsigo.admin.repository.InventoryRepo
import com.pepsigo.admin.screens.reports.DropDownList
import com.pepsigo.admin.utils.safeDate
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

enum class StockStatus {
    UNKNOWN,
    NO_STOCK,
    LOW_STOCK,
    IN_STOCK
}
enum class ExpiryStatus {
    UNKNOWN,
    NO_EXPIRY,
    EXPIRED,
    EXPIRING_SOON,
    NORMAL
}
private const val LOW_STOCK_THRESHOLD = 5
private const val EXPIRY_SOON_DAYS = 30L

class BatchStockUseCase(
    private val batchStockRepo: BatchStockRepo,
    private val inventoryRepo: InventoryRepo
){
    suspend fun getInventory(): Result<List<DropDownList>> {
        val result = inventoryRepo.fetchInventoryItems()
        return result.map { inventory ->
            inventory.map { DropDownList(it.id, it.name+"("+it.unit+")") }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBatchStock(id:Int?): Result<BatchStockData> {
        return batchStockRepo.getBatchStock(id).map{ batchStockData ->
            val batches = batchStockData.data.map { batch ->
                val parsedExpiryDate = batch.expiryDate.toLocalDateOrNull()
//                expiryDate = parsedExpiryDate.toString()

                BatchStockDetail(
                    batchId = batch.batchId,
                    itemName = batch.itemName,
                    expiryDate = batch.expiryDate.safeDate(),
                    expiryStatus = resolveExpiryStatus(parsedExpiryDate),
                    availableQuantity = batch.availableQuantity,
                    stockStatus = resolveStockStatus(batch.availableQuantity),
                    unit = batch.unit,
                    costPrice = batch.costPrice,
                    salePrice = batch.salePrice
                )
            }

            BatchStockData(
                count = batchStockData.count,
                totalAvailableQuantity = batches.sumOf { it.availableQuantity },
                data = batches
            )

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
private fun String?.toLocalDateOrNull(): LocalDate? {
    return try {
        this?.let {
            OffsetDateTime.parse(it).toLocalDate()
        }
    } catch (e: Exception) {
        null
    }
}

 fun resolveStockStatus(qty: Int): StockStatus =
    when {
        qty == 0 -> StockStatus.NO_STOCK
        qty <= LOW_STOCK_THRESHOLD -> StockStatus.LOW_STOCK
        else -> StockStatus.IN_STOCK
    }

@RequiresApi(Build.VERSION_CODES.O)
private fun resolveExpiryStatus(
    expiryDate: LocalDate?,
    today: LocalDate = LocalDate.now()
): ExpiryStatus {
    if (expiryDate == null) return ExpiryStatus.NO_EXPIRY

    return when {
        expiryDate.isBefore(today) -> ExpiryStatus.EXPIRED
        ChronoUnit.DAYS.between(today, expiryDate) <= EXPIRY_SOON_DAYS ->
            ExpiryStatus.EXPIRING_SOON
        else -> ExpiryStatus.NORMAL
    }
}