package com.pepsigo.admin.mapper


import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.PurchaseReportItem
import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.utils.safeDate

@RequiresApi(Build.VERSION_CODES.O)
fun PurchaseReportItem.toDomain(): SalesPurchaseReportUi {
    return SalesPurchaseReportUi(
        id = id,
        invoiceNumber = invoiceNumber ?: "N/A",
        userId = vendorId,
        madeByUserId = madeByUserId,
        saleDate = purchaseDate.safeDate(),
        subTotal = subTotal,
        discountBeforeTax = discountBeforeTax,
        tax = tax,
        discountAfterTax = discountAfterTax,
        totalAmount = total,
        invoiceStatus = invoiceStatus,
        name = vendor.name.ifBlank { "Unknown" },
        businessName = vendor.businessName ?: "N/A"
    )

}