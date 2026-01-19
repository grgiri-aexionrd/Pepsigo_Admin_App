package com.pepsigo.admin


import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.MapsInitializer
import android.util.Log
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.WindowRecomposerPolicy
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AdminAppApplication : Application() {
    lateinit var container: AppContainer

    @OptIn(InternalComposeUiApi::class, DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        Log.d("AdminAppApplication", "onCreate called")

        container = AppContainer(this)       // ✅ real API for release

    }
}
