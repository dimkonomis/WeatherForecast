plugins {
    id("com.app.weatherforecast.library")
    id("com.app.weatherforecast.compose")
    id("com.app.weatherforecast.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "com.app.weatherforecast.feature.location"

    defaultConfig {
        resValue("string", "google_map_api_key", properties["GOOGLE_MAPS_API_KEY"] as String)
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":contract:location"))
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.permissions)
    implementation(libs.location)
    implementation(libs.maps)
    implementation(libs.datastore)
    implementation(libs.kotlinx.serialization)

    testImplementation(project(":core:test"))
}