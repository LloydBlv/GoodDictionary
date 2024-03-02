// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    alias(libs.plugins.benchmark) apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
}