plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
  id("com.gooddictionary.hilt")
}

android {
  namespace = "com.example.dictionarysync"
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
}

dependencies {
  implementation(projects.libs.domain)
  testImplementation(projects.common.testing)
  implementation(libs.timber)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.datastore.preferences)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.turbine)
  testImplementation(libs.assertk)
  implementation(libs.androidx.hilt.work)
  ksp(libs.androidx.hilt.hilt.compiler)
  ksp(libs.dagger.hilt.compiler)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}
