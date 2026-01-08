package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.ReturnItems
import com.pepsigo.admin.screens.purchase.ReturnItemList

fun ReturnItemList.toApi(): ReturnItems {
    return ReturnItems(
        invId = invId,
        quantity = quantity.toInt()
    )

}