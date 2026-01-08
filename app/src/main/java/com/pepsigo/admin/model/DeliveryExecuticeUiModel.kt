package com.pepsigo.admin.model

data class DeliveryExecutiveUiModel(
    val id: Int,
    val name: String,
    val mobile: String,
    val email: String,
    val enabled: Boolean,
    val isToggling: Boolean = false,
    val status: String,
    val route: RouteUi?
)

data class RouteUi(
    val id: Int,
    val name: String,
    val assignmentStatus: String
)