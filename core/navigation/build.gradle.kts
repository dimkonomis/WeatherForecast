plugins {
    id("com.app.weatherforecast.library")
    id("com.app.weatherforecast.compose")
    id("kotlinx-serialization")
}

android {
    namespace = "com.app.weatherforecast.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization)
}