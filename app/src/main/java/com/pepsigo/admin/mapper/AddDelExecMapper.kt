package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.AddDeliveryExecDto
import com.pepsigo.admin.screens.deliveryExecutive.NewDelForm

fun NewDelForm.toDto(): AddDeliveryExecDto {
    return AddDeliveryExecDto(
        name = name,
        email = email,
        mobile = mobile,
        password = password
    )

}