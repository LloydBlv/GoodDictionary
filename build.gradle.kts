import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gooddictionary.root")
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    alias(libs.plugins.benchmark) apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    alias(libs.plugins.spotless) apply false
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("gooddictionary.enableComposeCompilerReports") == "true") {
                val path = layout.buildDirectory.asFile.map { it.absolutePath }.get()
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            path + "/compose_metrics"
                )

                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            path + "/compose_metrics"
                )
            }
        }
    }
}