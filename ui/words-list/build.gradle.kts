plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
  id("com.gooddictionary.hilt")
}

android {
  namespace = "com.example.wordslist"
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
  implementation(projects.feature.dictionarySync)

  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.timber)
  implementation(libs.androidx.ui)
  implementation(libs.androidx.material3)

  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.paging.compose)
  implementation(libs.core.ktx)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  testImplementation(libs.assertk)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.junit)
  testImplementation(libs.androidx.paging.testing)
  testImplementation(libs.androidx.core.testing)
  testImplementation(projects.common.testing)
  testImplementation(libs.mockk)
}
