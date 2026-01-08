package com.pepsigo.admin.screens.deliveryExecutive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pepsigo.admin.screens.customer.FormTextField

@Composable
fun DeliveryExecutiveAddScreen(
    viewModel: DeliveryExecutiveViewModel,
    state: DeliveryExecutiveUiState.AddDelForm,
    onSave: (NewDelForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Add Delivery Executive",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        FormTextField(
            value = state.form.name,
            onValueChange =  viewModel::onNameChange ,
            isError = state.formErrors.containsKey("name"),
            errorMessage = state.formErrors["name"],
            label = "Name",
            keyboardType = KeyboardType.Text
        )
        FormTextField(
            value = state.form.mobile,
            onValueChange =  viewModel::onMobileChange ,
            isError = state.formErrors.containsKey("mobile"),
            errorMessage = state.formErrors["mobile"],
            label = "Mobile",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            value = state.form.email,
            onValueChange =  viewModel::onEmailChange ,
            isError = state.formErrors.containsKey("email"),
            errorMessage = state.formErrors["email"],
            label = "Email",
            keyboardType = KeyboardType.Email
        )
        FormTextField(
            value = state.form.password,
            onValueChange =  viewModel::onPasswordChange ,
            isError = state.formErrors.containsKey("password"),
            errorMessage = state.formErrors["password"],
            label = "Password",
            keyboardType = KeyboardType.Password
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
//            Button(onClick = {}) {
//                Text(text = "Cancel")
//            }
            Button(onClick = {onSave(state.form)},
                enabled = !state.isLoading) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else
                Text(text = "Save")
            }
        }

    }

    BackHandler {
        viewModel.getDeliveryExecutives() // navigate back to list state
    }


}