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
    implementation(project(":core:ui"))
    implementation(project(":feature:location"))
    implementation(project(":feature:weather"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(libs.timber)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}