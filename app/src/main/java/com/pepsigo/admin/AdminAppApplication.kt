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


class AdminAppApplication: Application() {
    lateinit var container: AppContainer
//    lateinit var placesClient: PlacesClient
    @OptIn(InternalComposeUiApi::class, DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        Log.d("AdminAppApplication", "onCreate called")

//        container = if (BuildConfig.DEBUG) {
//            FakeAppContainer(this)   // ✅ fake data for debug/emulator
//        } else {
//            AppContainer(this)       // ✅ real API for release
//        }

        container = AppContainer(this)       // ✅ real API for release


        // 🔹 Create a single client for reuse
//        placesClient = Places.createClient(this)

        // 👇 Fix: force Maps to initialize on main thread
//        Handler(Looper.getMainLooper()).post {
//            MapsInitializer.initialize(applicationContext)
//        }


    }
}
