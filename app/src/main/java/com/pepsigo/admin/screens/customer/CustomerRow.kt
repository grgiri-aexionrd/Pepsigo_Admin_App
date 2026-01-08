package com.pepsigo.admin.screens.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.User

@Composable
fun CustomerRow(
    customer: User,
    onEditCustomer: (User) -> Unit,
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
            Text(customer.name, style = MaterialTheme.typography.bodyLarge)
            Text(customer.mobile, style = MaterialTheme.typography.bodyMedium)
            Text(customer.state, style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            IconButton(onClick = { onEditCustomer(customer) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            Switch(
                checked = customer.enabled,
                onCheckedChange = { onToggle(customer.id) }
            )
        }
    }
}

@Composable
fun CustomerDetailPopup(customer: User,
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
            Text(" ${customer.name} ", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(" Mobile: ${customer.mobile}", style = MaterialTheme.typography.bodyLarge)
            Text(" Email: ${customer.email}", style = MaterialTheme.typography.bodyLarge)
            Text(" Address:", style = MaterialTheme.typography.bodyLarge)

            Text("   ${customer.address1}" , style = MaterialTheme.typography.bodyLarge)
            Text("   ${customer.address2}" , style = MaterialTheme.typography.bodyLarge)
            Text("   ${customer.state } - ${customer.pincode }", style = MaterialTheme.typography.bodyLarge)
            Text(" Location: (${customer.latitude}, ${customer.longitude})",
                style = MaterialTheme.typography.bodyLarge
            )
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