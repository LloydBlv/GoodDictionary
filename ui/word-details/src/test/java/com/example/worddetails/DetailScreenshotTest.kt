package com.example.worddetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.domain.DictionaryWord
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7Pro)
class DetailScreenshotTest {
  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun wordLoadedTest() {
    composeRule.setContent {
      WordDetailContent(
        modifier = Modifier.fillMaxSize(),
        state = DetailViewModel.DetailUiState(
          isLoading = false,
          word = DictionaryWord(word = "Good word", 1),
          count = 0
        ),
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = {}
      )
    }
    composeRule
      .onRoot()
      .captureRoboImage()
  }
  @Test
  fun detailAppBarTest() {
    composeRule.setContent {
      WordDetailContent(
        modifier = Modifier.fillMaxSize(),
        state = DetailViewModel.DetailUiState(
          isLoading = false,
          word = DictionaryWord(word = "Good word", 1),
          count = 1000
        ),
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = {}
      )
    }
    composeRule
      .onNodeWithTag("detail_appbar_tag")
      .captureRoboImage()
  }
  @Test
  fun snackbarDismissedTest() {
    composeRule.setContent {
      WordDetailContent(
        modifier = Modifier.fillMaxSize(),
        state = DetailViewModel.DetailUiState(
          isLoading = false,
          word = DictionaryWord(word = "Good word", 1),
          count = 1000
        ),
        onBackPressed = {},
        onWordDeleted = {},
        onDeleteWordClicked = {}
      )
    }
    composeRule
      .onNodeWithContentDescription("Dismiss")
      .performClick()
    composeRule
      .onRoot()
      .captureRoboImage()
  }
}
