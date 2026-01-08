package com.pepsigo.admin.mapper

import com.pepsigo.admin.domainLayer.OfferUi
import com.pepsigo.admin.model.PromotionalOfferDto
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeInt

fun PromotionalOfferDto.toDomain(): OfferUi {
    return OfferUi(
        offerId = offerId,
        invId = inventoryId,
        itemName = itemName,
        itemQuantity = quantity.safeInt().toString(),
        itemPrice = salePrice.safeAmount(),
        unit = unit,
        autoAdd = autoAdd,
        canEdit = canEdit
    )

}