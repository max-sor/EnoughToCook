//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
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.enoughtocook"
    compileSdk = 34

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.example.enoughtocook"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {


    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    //implementation ("com.google.android.material:material:1.3.0-alpha01")
    implementation ("androidx.core:core-splashscreen:1.0.0")
    implementation ("com.aallam.openai:openai-client:3.2.0")
    implementation ("io.ktor:ktor-client-android:2.3.0")
    implementation("org.tensorflow:tensorflow-lite:2.9.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.2")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.2")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20210307")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.ai.client.generativeai:generativeai:0.1.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



}