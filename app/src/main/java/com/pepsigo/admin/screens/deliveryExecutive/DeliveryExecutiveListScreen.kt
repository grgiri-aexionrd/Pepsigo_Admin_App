package com.pepsigo.admin.screens.deliveryExecutive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.pepsigo.admin.model.DeliveryExecutiveUiModel
import com.pepsigo.admin.screens.customer.SearchBarSection
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun DeliveryExecutiveListScreen(
    deliveryExecutives: List<DeliveryExecutiveUiModel>,
    onEdit:(id: Int) -> Unit,
    onToggle: (deliveryExecutive: DeliveryExecutiveUiModel) -> Unit,
    modifier: Modifier = Modifier

) {
//    var selectedExec by rememberSaveable { mutableStateOf<DeliveryExecutiveUiModel?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredExec = deliveryExecutives.filter { deliveryExecutive ->
        searchQuery.isBlank() || deliveryExecutive.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier
        .fillMaxSize()) {
        LazyColumn {

            stickyHeader {
                SearchBarSection (
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

            }

            items(filteredExec) { deliveryExecutive ->
                DeliveryExecutiveRow(
                    deliveryExecutive = deliveryExecutive,
                    onEdit = onEdit,
                    onToggle = onToggle,
//                    onClick = { selectedExec = deliveryExecutive }
                )
            }
        }

    }

}

@Composable
fun DeliveryExecutiveRow(
    deliveryExecutive: DeliveryExecutiveUiModel,
    onEdit: (id: Int) -> Unit,
    onToggle: (deliveryExecutive: DeliveryExecutiveUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(deliveryExecutive.name, style = MaterialTheme.typography.bodyLarge)
            Text(deliveryExecutive.mobile, style = MaterialTheme.typography.bodyMedium)
            Text(deliveryExecutive.status, style = MaterialTheme.typography.bodyMedium)
            if (deliveryExecutive.route != null) {
                Text(
                    deliveryExecutive.route.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    deliveryExecutive.route.assignmentStatus,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
        Row {
            IconButton(onClick = { onEdit(deliveryExecutive.id) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            Switch(
                checked = deliveryExecutive.enabled,
                enabled = !deliveryExecutive.isToggling,
                onCheckedChange = { onToggle(deliveryExecutive) }
            )
        }

    }


}