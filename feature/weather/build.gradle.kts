plugins {
    id("com.app.weatherforecast.library")
    id("com.app.weatherforecast.compose")
    id("com.app.weatherforecast.hilt")
    id("com.app.weatherforecast.unit_test")
    id("com.app.weatherforecast.screenshot_test")
    id("kotlinx-serialization")
}

android {
    namespace = "com.app.weatherforecast.feature.weather"
}

dependencies {
    implementation(project(":contract:location"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:net"))
    implementation(project(":core:utils"))

    implementation(libs.kotlinx.serialization)
    implementation(libs.coil)
    implementation(libs.coil.okhttp)

    testImplementation(project(":core:test"))
    screenshotTestImplementation(libs.coil.test)
}