plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
  id("com.gooddictionary.hilt")
}

android {
  namespace = "com.example.worddetails"
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
  testImplementation(projects.common.testing)
  testImplementation(projects.libs.data)
  testImplementation(libs.androidx.room.ktx)
  testImplementation(libs.assertk)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.turbine)
  testImplementation(libs.robolectric)
  testImplementation(libs.hilt.android.testing)
  testImplementation(libs.androidx.core.testing)

  implementation(projects.libs.domain)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}
