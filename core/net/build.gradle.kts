plugins {
    id("com.app.weatherforecast.library")
    id("com.app.weatherforecast.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "com.app.weatherforecast.core.net"

    defaultConfig {
        buildConfigField("String", "API_KEY", properties["OPEN_WEATHER_API_KEY"] as String)
    }
}

dependencies {
    implementation(project(":core:utils"))

    api(libs.retrofit)
    api(libs.kotlinx.serialization)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.ok2curl)
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipper.soloader)
    debugImplementation(libs.flipper.network.plugin)
}