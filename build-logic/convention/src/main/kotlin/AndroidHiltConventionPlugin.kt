import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            dependencies {
                add("implementation", libraries.findLibrary("hilt.android").get())
                add("implementation", libraries.findLibrary("hilt.compose").get())
                add("ksp", libraries.findLibrary("hilt.compiler").get())
            }

        }
    }

}