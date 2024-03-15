plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
  id("com.gooddictionary.hilt")
}

android {
  namespace = "com.example.data"

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
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.paging.common.ktx)

  ksp(libs.androidx.hilt.hilt.compiler)
  ksp(libs.dagger.hilt.compiler)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.datastore.preferences)

  implementation(libs.androidx.hilt.work)
  implementation(libs.androidx.work.runtime.ktx)

  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.timber)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  testImplementation(projects.common.testing)
  testImplementation(libs.androidx.paging.testing)
  testImplementation(libs.turbine)
  testImplementation(libs.androidx.runner)
  testImplementation(libs.core.ktx)
  testImplementation(libs.junit)
  testImplementation(libs.androidx.room.testing)
  testImplementation(libs.androidx.core.testing)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.assertk)
}
