package com.pepsigo.admin.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditProfileScreen(
    profile: UserProfileUiModel,
    onSave: (UserProfileUiModel) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf(profile.businessName) }
    var name by remember { mutableStateOf(profile.name) }
    var mobile by remember { mutableStateOf(profile.mobile) }
    var email by remember { mutableStateOf(profile.email) }
    var address1 by remember { mutableStateOf(profile.address1) }
    var address2 by remember { mutableStateOf(profile.address2) }
    var state by remember { mutableStateOf(profile.state) }
    var pincode by remember { mutableStateOf(profile.pincode) }
    var latitude by remember { mutableStateOf(profile.latitude) }
    var longitude by remember { mutableStateOf(profile.longitude) }
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        EditableTextField(
            label = "Business name",
            value = businessName,
            onValueChange = { businessName = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Name",
            value = name,
            onValueChange = { name = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Mobile",
            value = mobile,
            onValueChange = { mobile = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Address 1",
            value = address1,
            onValueChange = { address1 = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Address 2",
            value = address2,
            onValueChange = { address2 = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "State",
            value = state,
            onValueChange = { state = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Pincode",
            value = pincode,
            onValueChange = { pincode = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Latitude",
            value = latitude,
            onValueChange = { latitude = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableTextField(
            label = "Longitude",
            value = longitude,
            onValueChange = { longitude = it }
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center


        ) {
            Button(onClick = {
                onSave(
                    profile.copy(
                        businessName = businessName,
                        name = name,
                        mobile = mobile,
                        email = email,
                        address1 = address1,
                        address2 = address2,
                        state = state,
                        pincode = pincode,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }) {
                Text(text = "Save")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onCancel) {
                Text(text = "Cancel")
            }
        }
    }
}

@Composable
fun EditableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth()
    )
}