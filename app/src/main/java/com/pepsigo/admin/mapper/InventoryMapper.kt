package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.InventoryItem
import com.pepsigo.admin.model.InventoryListUi

fun InventoryItem.toDomain(): InventoryListUi {
    return InventoryListUi(
        id = id,
        name = name,
        unit = unit,
        gstPercent = gstPercent,
        enabled = enabled
    )

}