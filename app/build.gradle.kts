plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "huce.fit.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "huce.fit.myapplication"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    
    // Google Sign-In
    implementation(libs.play.services.auth)
    
    // Swipe to Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // ZaloPay SDK - Sử dụng file cục bộ trong thư mục libs
    implementation(fileTree(mapOf(
        "dir" to "D:\\Download\\APPcode\\BTN_AppMobile",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
}