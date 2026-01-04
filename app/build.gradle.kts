plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bytelift"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bytelift"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

// 👉 ALL THESE GO INSIDE THIS BLOCK, NOT ABOVE
dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase Auth (you were already using this)
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In (already present before)
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // 🔥 Firestore for leaderboard
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.mikhaellopez:circularimageview:4.3.0")

    // CircleImageView for profile picture
    implementation("de.hdodenhof:circleimageview:3.1.0")

// Gson for RoutineStorage
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
