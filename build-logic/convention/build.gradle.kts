import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.app.weatherforecast.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.app.weatherforecast.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.app.weatherforecast.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "com.app.weatherforecast.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "com.app.weatherforecast.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidUnitTest") {
            id = "com.app.weatherforecast.unit_test"
            implementationClass = "AndroidUnitTestConventionPlugin"
        }
        register("androidScreenshotTest") {
            id = "com.app.weatherforecast.screenshot_test"
            implementationClass = "AndroidScreenShotTestConventionPlugin"
        }
    }
}