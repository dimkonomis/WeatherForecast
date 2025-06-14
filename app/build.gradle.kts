plugins {
    id("com.app.weatherforecast.application")
    id("com.app.weatherforecast.hilt")
}

android {
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":contract:location"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":feature:location"))
    implementation(project(":feature:weather"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.material)
    implementation(libs.timber)
    testImplementation(libs.junit)
    testImplementation(project(":core:test"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}