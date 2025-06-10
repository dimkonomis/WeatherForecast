import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

val Project.application: ApplicationExtension
    get() = extensions.getByType(ApplicationExtension::class.java)

val Project.android: LibraryExtension
    get() = extensions.getByType(LibraryExtension::class.java)

val Project.kotlin: KotlinAndroidProjectExtension
    get() = extensions.getByType(KotlinAndroidProjectExtension::class.java)

val Project.libraries: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")