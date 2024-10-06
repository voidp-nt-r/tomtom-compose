import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    dependencies {
        classpath(libs.dokka.plugin)
        classpath(libs.dokka.base)
    }

}


subprojects {
    tasks.withType<DokkaTask>().configureEach {
        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "This is an <b>unofficial</b> library. For recommended practices and official guidance on using the Map SDK, please refer to the official <a href='https://developer.tomtom.com/maps/android/introduction/introduction'>TomTom Developer Portal.</a>"
            customStyleSheets = listOf(
                file("../documentation-assets/tomtom-dokka-styles.css")
            )
            separateInheritedMembers = true
            mergeImplicitExpectActualDeclarations = true
        }
    }
}


plugins {
    alias(libs.plugins.dokka) apply true
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
}