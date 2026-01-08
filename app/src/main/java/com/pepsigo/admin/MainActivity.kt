package com.pepsigo.admin

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pepsigo.admin.ui.theme.AdminAppTheme


class MainActivity : ComponentActivity() {
    // Grab your container from Application
    private val appContainer: AppContainer
        get() = (application as AdminAppApplication).container

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            AdminAppTheme {
                // Pass dependencies into StartApp
                StartApp(
                    userPreferenceRepository = appContainer.userPreferenceRepository
                )
            }
        }
    }
}


