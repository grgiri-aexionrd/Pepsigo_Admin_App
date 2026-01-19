package com.pepsigo.admin.screens.customer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pepsigo.admin.model.User
import com.pepsigo.admin.screens.reports.DropDownErrorCard


@Composable
fun CustomerListScreen(
    customers: List<User>,
    message: String?,
    onEditCustomer: (User) -> Unit,
    onToggle: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCustomer by rememberSaveable { mutableStateOf<User?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }


    val filteredCustomers = customers.filter { customer ->
        searchQuery.isBlank() || customer.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            // 🔍 Sticky Search Bar
            stickyHeader {
                SearchBarSection(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            if(message != null) {
                item {
                    DropDownErrorCard(message)
                }
            }else {
                // 📋 List of customers
                items(filteredCustomers) { customer ->
                    CustomerRow(
                        customer = customer,
                        onEditCustomer = onEditCustomer,
                        onToggle = onToggle,
                        onClick = { selectedCustomer = customer }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            }
        }
        // 👇 popup dialog
        selectedCustomer?.let { customer ->
            Dialog(onDismissRequest = { selectedCustomer = null }) {
                CustomerDetailPopup(customer = customer, onClose = { selectedCustomer = null })
            }
        }
    }


@Composable
fun SearchBarSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(50),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Search ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            }
        )
    }
}






