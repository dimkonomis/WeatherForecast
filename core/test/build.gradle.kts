plugins {
    id("com.app.weatherforecast.library")
    id("com.app.weatherforecast.unit_test")
}

android {
    namespace = "com.app.weatherforecast.core.test"
}

dependencies {
    implementation(project(":core:net"))
    implementation(project(":core:utils"))
}
