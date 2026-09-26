import com.android.build.api.dsl.ViewBinding

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.semesterproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.semesterproject"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.github.glide)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.firebase.database)
    implementation(libs.activity)
    implementation(libs.generativeai.v040)




}