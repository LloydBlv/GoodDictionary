plugins {
  id("com.gooddictionary.kotlin.android")
  id("com.gooddictionary.android.library")
}

android {
  namespace = "com.example.testing"
  compileSdk = 34

  defaultConfig {
    minSdk = 21

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
  implementation(projects.libs.data)
  implementation(projects.libs.domain)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.timber)
  implementation(libs.androidx.paging.common.ktx)
  implementation(libs.androidx.paging.testing)

  implementation(libs.kotlinx.coroutines.test)
  implementation(libs.turbine)
  implementation(libs.assertk)
  implementation(libs.junit)
}
