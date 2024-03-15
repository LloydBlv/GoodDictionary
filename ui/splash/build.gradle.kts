plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
  id("com.gooddictionary.hilt")
}

android {
  namespace = "com.example.splash"
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
  buildFeatures.compose = true
  composeOptions { kotlinCompilerExtensionVersion = "1.5.9" }
}

dependencies {

  implementation(projects.libs.domain)

  implementation(libs.androidx.work.runtime.ktx)

  debugImplementation(libs.androidx.ui.test.manifest)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  testImplementation(projects.common.testing)
  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.robolectric)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.turbine)
  testImplementation(libs.assertk)
  testImplementation(libs.androidx.ui.test.junit4)
}
