package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.VendorDues
import com.pepsigo.admin.model.VendorDuesUi
import com.pepsigo.admin.utils.safeAmount

fun VendorDues.toDomain(): VendorDuesUi {
    return VendorDuesUi(
        id = vendorId,
        name = vendorName,
        totalPurchase = totalPurchases.safeAmount(),
        paid = paymentReceived.safeAmount(),
        balance = outstandingDues.safeAmount()
    )
}