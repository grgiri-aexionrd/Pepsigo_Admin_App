package com.pepsigo.admin.screens.routes


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.model.LocationUiModel
import com.pepsigo.admin.model.RouteFormState
import com.pepsigo.admin.ui.theme.inversePrimaryLight


@Composable
fun RouteFormScreen(
    formState: RouteFormState,
    onNameChange: (String) -> Unit,
    onLocationToggle: (String) -> Unit,
    onLocationMove: (Int, Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
//        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onSave,
                ) {
                    Text("Save")
                }
            }
        },
//        containerColor = inversePrimaryLight.copy(alpha = 0.35f),
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Route name input
            OutlinedTextField(
                value = formState.routeName,
                onValueChange = onNameChange,   // send to ViewModel
                isError = formState.formError,
                label = { Text("Route Name") },
                modifier = Modifier.fillMaxWidth()
            )
            if (formState.formError){
                Text(
                    text = formState.formErrorMessage ?: "Error",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Locations selection + reorderable
            LocationAssignToRoute(
                locations = formState.locations,
                onLocationsChange = onLocationToggle,
                onMove = onLocationMove,
                errorText = formState.errorMessage
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}