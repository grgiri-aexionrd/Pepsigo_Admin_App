import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id ("com.google.gms.google-services")

    kotlin("plugin.serialization") version "2.2.20"

}

android {
    namespace = "com.pepsigo.admin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pepsigo.admin"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"${project.findProperty("MAPS_API_KEY") ?: ""}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}
secrets {
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)
    implementation(libs.places)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.ui)
    implementation(libs.androidx.compose.animation.core)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Lifecycle ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Lifecycle Runtime (needed for Compose integration)
    implementation(libs.androidx.lifecycle.runtime.ktx)

//    Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

//    okhttp
    implementation(libs.okhttp)
    // logging interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

//    Kotlinx serialization
    implementation(libs.kotlinx.serialization.json)


    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose)

//    Datastore
    implementation(libs.androidx.datastore.preferences)

//    Google maps
    implementation("com.google.maps.android:maps-compose:6.12.1")
    implementation("com.google.maps.android:maps-compose-utils:6.12.1")
    implementation(libs.places)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // Explicitly add play-services-maps to avoid mismatch
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // Splash Screen API for a smooth app launch experience
    implementation("androidx.core:core-splashscreen:1.0.1")

//  drag and drop
    implementation("sh.calvin.reorderable:reorderable:3.0.0")


//    paging library
    implementation("androidx.paging:paging-runtime:3.3.6")
    // optional - Jetpack Compose integration
    implementation("androidx.paging:paging-compose:3.4.0-alpha04")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation ("com.google.firebase:firebase-messaging")

    // accompanist - permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
}


