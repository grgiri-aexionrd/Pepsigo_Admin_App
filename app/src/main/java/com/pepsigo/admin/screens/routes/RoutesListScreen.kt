package com.pepsigo.admin.screens.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pepsigo.admin.model.RouteUiModel
import com.pepsigo.admin.screens.commonComponents.DropDown
import com.pepsigo.admin.screens.customer.SearchBarSection
import com.pepsigo.admin.ui.theme.inversePrimaryLight

@Composable
fun RoutesListScreen(
    routes: List<RouteUiModel>,
    onAssignRouteClick: (RouteUiModel) -> Unit,
    onEditRoute: (RouteUiModel) -> Unit,
    onToggle: (RouteUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRoute by remember { mutableStateOf<RouteUiModel?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }


    val filteredRoutes = routes.filter { route ->
        searchQuery.isBlank() || route.routeName.contains(searchQuery, ignoreCase = true)
    }

        LazyColumn(modifier = modifier
            .background(
                color = inversePrimaryLight.copy(alpha = 0.35f)
            )
            .fillMaxSize()
            .padding(16.dp)) {

            // 🔍 Sticky Search Bar
            stickyHeader {
                SearchBarSection(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            items(filteredRoutes) { route ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRoute = route } // 👈 open popup
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(route.routeName, style = MaterialTheme.typography.bodyLarge)
                    }

                    Row {
                        IconButton(onClick = {
                            onAssignRouteClick(route)
                             }
                        ) {
                            Icon(Icons.Default.PersonAddAlt1, contentDescription = "Add")
                        }
                        IconButton(onClick = { onEditRoute(route) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        Switch(
                            checked = route.enabled,
                            enabled = !route.isToggling,
                            onCheckedChange = {  onToggle(route) }
                        )
                    }
                }
            }
        }



        // 👇 popup dialog
        selectedRoute?.let { route ->
            Dialog(onDismissRequest = { selectedRoute = null }) {
                RouteDetailPopup(route = route, onClose = { selectedRoute = null })
            }
        }
    }

