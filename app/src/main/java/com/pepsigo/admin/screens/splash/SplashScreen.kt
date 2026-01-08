package com.pepsigo.admin.screens.splash


import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel,
    onNavigateLogin: () -> Unit,
    onNavigateHome: () -> Unit,

) {

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    LoadingScreen()

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            is LogInState.LoggedIn -> {
                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                onNavigateHome()
            }

            is LogInState.LoggedOut -> {
                Toast.makeText(context, (isLoggedIn as LogInState.LoggedOut).msg, Toast.LENGTH_SHORT).show()
                onNavigateLogin()
            }

            else -> {
                // Still loading, do nothing
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Manjunatha Agencies",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "verifying...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}







