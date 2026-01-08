package com.pepsigo.admin.model

import com.pepsigo.admin.utils.AppError

data class ExecutivesResult(
    val data: List<DeliveryExecutiveUiModel>,
    val error: AppError? = null
)