import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.gms.google-services") version "4.4.2"
}

android {
    namespace = "com.unimib.workingspot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.unimib.workingspot"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Android base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.shimmer)
    implementation(libs.material)

    // Jetpack Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Google Play Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Firebase (via BoM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.com.google.firebase.firebase.auth)
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.database)

    // Credential Manager and Google Identity
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.androidx.credentials.vlatestversion)
    implementation(libs.androidx.credentials.play.services.auth.vlatestversion)
    implementation(libs.googleid.vlatestversion)
    implementation(libs.googleid)

    implementation(libs.androidx.swiperefreshlayout)


    // Room DB
    implementation(libs.room.runtime)
    implementation(libs.room.common)
    implementation(libs.play.services.auth)
    annotationProcessor(libs.room.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.cronet.embedded)

    // Commons
    implementation(libs.commons.validator)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.rxjava3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}