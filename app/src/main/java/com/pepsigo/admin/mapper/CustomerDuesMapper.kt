package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.CustomerDues
import com.pepsigo.admin.model.CustomerDuesUi
import com.pepsigo.admin.utils.safeAmount

fun CustomerDues.toDomain(): CustomerDuesUi {
    return CustomerDuesUi(
        id = customerId,
        name = customerName,
        totalSales = totalSales.safeAmount(),
        paid = paymentReceived.safeAmount(),
        due = outstandingDues.safeAmount()
    )

}