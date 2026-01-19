package com.pepsigo.admin.screens.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.screens.customer.SearchBarSection
import com.pepsigo.admin.screens.home.MetricCardError

@Composable
fun InventoryListScreen(
    inventoryItems: List<InventoryListUi>,
    message: String?,
    onClick: (InventoryListUi) -> Unit,
    onEdit: (InventoryListUi) -> Unit,
    onToggle: (id: Int) -> Unit,
    modifier: Modifier = Modifier
//    onRefresh: () -> Unit = {}
) {
//    var selectedInventoryItem by rememberSaveable { mutableStateOf<InventoryListUi?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredInventory = inventoryItems.filter { item ->
        searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()){
        LazyColumn {
            // 🔍 Sticky Search Bar
            stickyHeader {
                SearchBarSection(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            if( message != null && inventoryItems.isEmpty() ) {
                item {
                    MetricCardError(message)
                }
            }else {
                // 📋 List of Inventory
                items(filteredInventory) { inventoryItem ->
                    InventoryRow(
                        inventoryItem = inventoryItem,
                        onEdit = onEdit,
                        onToggle = onToggle,
                        onClick = onClick
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

        }
    }


}

@Composable
fun InventoryRow(
    inventoryItem: InventoryListUi,
    onEdit: (InventoryListUi) -> Unit,
    onToggle: (id: Int) ->Unit,
    onClick: (InventoryListUi) ->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(inventoryItem) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column{
            Text(inventoryItem.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row{
                Text("Unit", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(56.dp))
                Text("GST",style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Row{
                Text(inventoryItem.unit, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(56.dp))
                Text(inventoryItem.gstPercent.toString(), style = MaterialTheme.typography.bodyLarge)
            }

        }
        Row {
            IconButton(onClick = { onEdit(inventoryItem) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            Switch(
                checked = inventoryItem.enabled,
                enabled = !inventoryItem.isToggling,
                onCheckedChange = { onToggle(inventoryItem.id) }
            )
        }

    }


}