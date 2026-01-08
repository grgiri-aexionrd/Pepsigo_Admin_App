package com.pepsigo.admin.model

data class ExecutiveStatus(
    val id: Int,
    val delExecName: String,
    val status: String,
    val route: RouteStatus?
)
data class RouteStatus(
    val id: Int,
    val name: String,
    val assignmentStatus: String
)