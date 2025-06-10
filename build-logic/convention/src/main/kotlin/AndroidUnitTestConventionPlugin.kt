import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidUnitTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            android.apply {
                testBuildType = "debug"
            }
            dependencies {
                add("api", libraries.findLibrary("junit").get())
                add("api", libraries.findLibrary("turbine").get())
                add("api", libraries.findLibrary("mockk").get())
                add("api", libraries.findLibrary("kotlinx.coroutines.test").get())
            }
        }
    }
}