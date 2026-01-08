package com.pepsigo.admin.screens.routes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.domainLayer.DeliveryExecutiveAssignmentStatus
import com.pepsigo.admin.model.RouteUiModel
import com.pepsigo.admin.screens.commonComponents.DropDown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignRouteBottomSheet(
    route: RouteUiModel,
    delExec: AssignDeliveryExecutive,
    assignError: String?,
    updateSelected: (FreeDeliveryExecutive?) -> Unit,
    onAssignSave: (RouteUiModel, FreeDeliveryExecutive?) -> Unit,
    onDismiss: () -> Unit
){
    val freeExec = delExec.deliveryExecutives.filter { it.status == DeliveryExecutiveAssignmentStatus.FREE }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },

    ){
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())

        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Assign Delivery Executive",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Route, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Route:  ${route.routeName}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
            if (freeExec.isEmpty()) {
                Text(
                    text = "No free delivery executives available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Delivery Executive",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    DropDown(
                        dropDown = freeExec,
                        error = delExec.selectedError,
                        label = "",
                        selected = delExec.selected,
                        onSelected = { exec -> updateSelected(exec) },
                        labelExtractor = { it.delExecName },
                        isCreatePurchase = true

                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (assignError != null) {
                    Text(
                        text = assignError,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Button(
                    onClick = { onAssignSave(route, delExec.selected) },
                    modifier = Modifier.wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Save")
                }
            }
        }

    }

}