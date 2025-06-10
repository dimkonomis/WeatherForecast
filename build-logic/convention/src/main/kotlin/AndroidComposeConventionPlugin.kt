import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            android.apply {

                buildFeatures {
                    compose = true
                }

            }

            dependencies {
                add("implementation", platform(libraries.findLibrary("androidx.compose.bom").get()))
                add("implementation", libraries.findLibrary("androidx.compose.ui").get())
                add("implementation", libraries.findLibrary("androidx.compose.ui.tooling.preview").get())
                add("implementation", libraries.findLibrary("androidx.compose.material3").get())
                add("debugImplementation", libraries.findLibrary("androidx.compose.ui.tooling").get())
                add("debugImplementation", libraries.findLibrary("androidx-compose-ui-test").get())
            }
        }
    }

}