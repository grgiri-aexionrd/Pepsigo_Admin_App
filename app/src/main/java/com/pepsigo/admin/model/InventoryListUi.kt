package com.pepsigo.admin.model

data class InventoryListUi(
    val id: Int,
    val name: String,
    val unit: String,
    val gstPercent: Double,
    val enabled: Boolean,
    val isToggling: Boolean = false
)
