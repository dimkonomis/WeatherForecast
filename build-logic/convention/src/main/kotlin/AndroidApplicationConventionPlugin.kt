import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            application.apply {
                namespace = ProjectConfig.applicationId
                compileSdk = ProjectConfig.compileSdk

                defaultConfig {
                    targetSdk = ProjectConfig.targetSdk
                    minSdk = ProjectConfig.minSdk
                    versionCode = 1
                    versionName = "1.0"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                buildFeatures {
                    buildConfig = true
                    compose = true
                }

                compileOptions {
                    isCoreLibraryDesugaringEnabled = true
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                packaging {
                    resources {
                        excludes += "META-INF/*"
                    }
                }

            }

            kotlin.apply {
                compilerOptions {
                    jvmTarget.set(JVM_17)
                    // We need to add this due to error
                    // Class is compiled by a pre-release version of Kotlin and cannot be loaded by this version of the compiler
                    // after enabling ExplicitBackingFields feature
                    freeCompilerArgs.add("-Xskip-prerelease-check")
                }
            }

            dependencies {
                add("coreLibraryDesugaring", libraries.findLibrary("desugar").get())
            }

        }
    }
}