package com.pepsigo.admin.screens.vendors

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.pepsigo.admin.model.User
import com.pepsigo.admin.screens.customer.SearchBarSection

@Composable
fun VendorListScreen(
    vendors: List<User>,
    onEditVendor: (User) -> Unit,
    onToggle: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var selectedVendor by rememberSaveable { mutableStateOf<User?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredVendors = vendors.filter { vendor ->
        searchQuery.isBlank() || vendor.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {

            // 🔍 Sticky Search Bar written in  customer screen
            stickyHeader {
                SearchBarSection(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
            // 📋 List of vendors
            items(filteredVendors) { vendor ->
                VendorRow(
                    vendor = vendor,
                    onEditVendor = onEditVendor,
                    onToggle = onToggle,
                    onClick = { selectedVendor = vendor }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }

    // 👇 popup dialog
    selectedVendor?.let { vendor ->
        Dialog(onDismissRequest = { selectedVendor = null }) {
            VendorDetailPopup(vendor = vendor, onClose = { selectedVendor = null })
        }

    }

}

@Composable
fun VendorRow(
    vendor: User,
    onEditVendor: (User) -> Unit,
    onToggle: (id: Int) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(vendor.name, style = MaterialTheme.typography.bodyLarge)
            Text(vendor.mobile, style = MaterialTheme.typography.bodyMedium)
            Text(vendor.state, style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            IconButton(onClick = { onEditVendor(vendor) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            Switch(
                checked = vendor.enabled,
                onCheckedChange = { onToggle(vendor.id) }
            )
        }
    }
}

@Composable
fun VendorDetailPopup(vendor: User,
                        onClose: () -> Unit) {
    Surface (
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Column(Modifier.padding(16.dp)) {
            Spacer(Modifier.height(8.dp))
            Text(" ${vendor.name} ", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(" Mobile: ${vendor.mobile}", style = MaterialTheme.typography.bodyLarge)
            Text(" Email: ${vendor.email}", style = MaterialTheme.typography.bodyLarge)
            Text(" Address:", style = MaterialTheme.typography.bodyLarge)

            Text("   ${vendor.address1}" , style = MaterialTheme.typography.bodyLarge)
            Text("   ${vendor.address2}" , style = MaterialTheme.typography.bodyLarge)
            Text("   ${vendor.state } - ${vendor.pincode }", style = MaterialTheme.typography.bodyLarge)
//            Text(" Location: (${vendor.latitude}, ${vendor.longitude})",
//                style = MaterialTheme.typography.bodyLarge
//            )
            Spacer(Modifier.padding(16.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}