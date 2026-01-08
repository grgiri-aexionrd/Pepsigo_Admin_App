package com.pepsigo.admin.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.DrawerGroup
import com.pepsigo.admin.model.DrawerItem

@Composable
fun DrawerContentList(
    modalDrawerGroups: List<DrawerGroup>,
    onItemClicked: (DrawerItem) -> Unit,
) {
    modalDrawerGroups.forEach { group ->
        Text(
            text = group.title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                bottom = 4.dp
            )
        )

        group.items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false, // hook up with navController.currentDestination later
                onClick = {
                    onItemClicked(item)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        HorizontalDivider()
    }
}