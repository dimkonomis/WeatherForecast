import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }
            android.apply {
                compileSdk = ProjectConfig.compileSdk

                defaultConfig {
                    minSdk = ProjectConfig.minSdk
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildFeatures {
                    buildConfig = true
                }

                compileOptions {
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
                sourceSets.all {
                    languageSettings.enableLanguageFeature("ExplicitBackingFields")
                }
                compilerOptions {
                    jvmTarget.set(JVM_17)
                    // We need to add this due to error
                    // Class is compiled by a pre-release version of Kotlin and cannot be loaded by this version of the compiler
                    // after enabling ExplicitBackingFields feature
                    freeCompilerArgs.add("-Xskip-prerelease-check")
                }
            }
            dependencies {
                add("implementation", libraries.findLibrary("timber").get())
                add("implementation", libraries.findLibrary("kotlinx.coroutines").get())
            }
        }
    }
}