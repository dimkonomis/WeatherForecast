import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidScreenShotTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.compose.screenshot")
            }
            android.apply {
                testOptions {
                    unitTests {
                        isReturnDefaultValues = true
                    }
                }
                experimentalProperties["android.experimental.enableScreenshotTest"] = true
            }

            dependencies {
                add("screenshotTestImplementation", libraries.findLibrary("androidx.compose.ui.tooling").get())
            }
        }
    }
}