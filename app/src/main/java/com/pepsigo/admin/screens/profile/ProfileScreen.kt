package com.pepsigo.admin.screens.profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pepsigo.admin.utils.AppError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackToProfileScreen: () -> Unit,
    onNavigateBackToHome: () -> Unit,
    onLocationUpdate: () -> Unit
) {
    val profile by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (profile) {
                            is ProfileUiState.EditProfile -> "Edit Profile"
                            else -> "Profile"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick =
                        {
                            when (profile) {
                                is ProfileUiState.EditProfile -> {
                                    onBackToProfileScreen()
                                }
                                else -> {
                                    onNavigateBackToHome()
                                }
                            }
                            }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        when (profile) {
            is ProfileUiState.Loading ->{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()               }

            }

            is ProfileUiState.Loaded ->{
                val loadedProfile = profile as ProfileUiState.Loaded
                ProfileDetailsContent(
                    profile = loadedProfile.profile,
                    onEditEmailClick = { viewModel.onEditEmailClick(loadedProfile.profile.email) },
                    onEditProfileClick = { viewModel.onEditProfileClick(loadedProfile.profile) },
                    onChangePasswordClick = {viewModel.onChangePasswordClick()},
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ProfileUiState.EditEmail -> {
                val editEmailState = profile as ProfileUiState.EditEmail
                EditEmailScreen(
                    currentEmail = editEmailState.email,
                    isError = editEmailState.isError,
                    error = editEmailState.error,
                    onDismiss = { viewModel.getProfile() },
                    onEmailUpdated = { newEmail, password->
                        viewModel.updateEmail(newEmail,password)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ProfileUiState.EditProfile -> {
                val editProfileState  = profile as ProfileUiState.EditProfile
                // 🔹 Intercept phone back button while editing
                BackHandler {
                    viewModel.getProfile() // reset state back to Profile view
                }
                EditProfileScreen(
                    editProfileState.profile,
                    onSave ={ updatedProfile ->
                        viewModel.updateProfile(updatedProfile) },
                    onCancel ={ viewModel.getProfile() },
                    onLocationUpdate = onLocationUpdate,
                    modifier = Modifier.padding(innerPadding)
                )

            }

            is ProfileUiState.Success -> {
                LaunchedEffect((profile as ProfileUiState.Success).message){
                    Toast.makeText(context, (profile as ProfileUiState.Success).message, Toast.LENGTH_LONG).show()

                }
            }

            is ProfileUiState.ChangePassword -> {
                ChangePasswordDialog(
                    onDismiss = { viewModel.getProfile() },
                    onConfirm = { currentPassword, newPassword ->
                        viewModel.updatePassword(currentPassword, newPassword)
                    }
                )

            }

            else -> {
                val error = (profile as ProfileUiState.Error).error
                val message = when (error) {
                    is AppError.Network -> "🌐 ${error.message}"
                    is AppError.Server -> "⚠️ Server (${error.code}): ${error.message}"
                    is AppError.Unknown -> "❓ ${error.message}"
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
            }
        }

    }
}

@Composable
fun EditEmailScreen(
    currentEmail: String,
    isError: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onEmailUpdated: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false // allow custom width
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = modifier.fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Edit Email",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.padding(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("New Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.padding(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Current Password") },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "password Icon",
                                tint = Color.Gray
                            )
                        }

                    },
                    isError = isError,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onEmailUpdated(email,password) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}