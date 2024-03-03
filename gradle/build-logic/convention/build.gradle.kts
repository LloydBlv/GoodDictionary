plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
    }

    kotlinGradle {
        target("*.kts")
        ktlint()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("root") {
            id = "com.gooddictionary.root"
            implementationClass = "com.example.gooddictionary.RootConventionPlugin"
        }
        register("kotlinAndroid") {
            id = "com.gooddictionary.kotlin.android"
            implementationClass = "com.example.gooddictionary.KotlinAndroidConventionPlugin"
        }
        register("androidApplication") {
            id = "com.gooddictionary.android.application"
            implementationClass = "com.example.gooddictionary.AndroidApplicationConventionPlugin1"
        }
        register("androidLibrary") {
            id = "com.gooddictionary.android.library"
            implementationClass = "com.example.gooddictionary.AndroidLibraryConventionPlugin"
        }
        register("hiltAndroid") {
            id = "com.gooddictionary.hilt"
            implementationClass = "com.example.gooddictionary.HiltConventionPlugin"
        }
    }
}