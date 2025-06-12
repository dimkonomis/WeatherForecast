plugins {
    id("com.app.weatherforecast.library")
    id("kotlinx-serialization")
}

android {
    namespace = "com.app.weatherforecast.contract.location"
}

dependencies {
    implementation(libs.kotlinx.serialization)
}
