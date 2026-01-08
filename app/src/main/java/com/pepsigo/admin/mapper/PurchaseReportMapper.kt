package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.model.SalesReportItem
import com.pepsigo.admin.utils.safeDate

fun SalesReportItem .toDomain(): SalesPurchaseReportUi {
    return SalesPurchaseReportUi(
        id = id,
        invoiceNumber = invoiceNumber?: "N/A",
        userId = customerId,
        madeByUserId = madeByUserId,
        saleDate = saleDate.safeDate(),
        subTotal = subTotal,
        discountBeforeTax = discountBeforeTax,
        tax = tax,
        discountAfterTax = discountAfterTax,
        totalAmount = total,
        invoiceStatus = invoiceStatus,
        name = customer.name.ifBlank { "Unknown" },
        businessName = customer.businessName ?: "N/A"
    )

}