import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gooddictionary.root")
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.benchmark) apply false
    alias(libs.plugins.hilt) apply false
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