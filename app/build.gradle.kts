plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.application")
  id("com.gooddictionary.hilt")
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.example.myapplication"

  defaultConfig {
    applicationId = "com.example.myapplication"
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.9"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(projects.ui.wordsList)
  implementation(projects.ui.wordDetails)
  implementation(projects.ui.splash)
  implementation(projects.feature.dictionarySync)

  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  implementation(libs.timber)

  runtimeOnly(projects.libs.data)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.navigation.compose)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
