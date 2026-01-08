package com.pepsigo.admin.model

import androidx.compose.ui.graphics.vector.ImageVector



data class DrawerGroup(
    val title: String,
    val items: List<DrawerItem>
)
data class DrawerItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)



