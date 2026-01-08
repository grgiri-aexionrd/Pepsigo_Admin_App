package com.pepsigo.admin.screens.routes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.LocationUiModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun LocationAssignToRoute(
    locations: List<LocationUiModel>,
    onLocationsChange: (String) -> Unit,
    onMove: (Int, Int) -> Unit,
    errorText: String? = null
) {


    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState( lazyListState ){ from, to ->
        onMove(from.index, to.index)
        }


    Text("Locations ", style = MaterialTheme.typography.titleMedium)

    Card {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if( locations.isEmpty() ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),

                    ) {
                        Text(
                            text = errorText  ?: "No Locations Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                        )
                        Text(
                            text = "Pull down to retry",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }else {
                itemsIndexed(locations, key = { _, item -> item.id }) { _, item ->
                    ReorderableItem(reorderState, key = item.id) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                        Surface(
                            shadowElevation = elevation,
                            modifier = Modifier.animateItem()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isSelected,
                                    onCheckedChange = { onLocationsChange(item.id) }
                                )
                                Text(item.name, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = "Reorder",
                                    modifier = Modifier.draggableHandle()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}