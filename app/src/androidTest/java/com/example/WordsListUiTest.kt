package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WordsListUiTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()
  @Test
  fun testSplashElementsAreShown() {
    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.onNodeWithTag("splash_text_tag", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("splash_loading_test_tag", useUnmergedTree = true).assertIsDisplayed()
  }

  @FlakyTest
  @Test
  fun testSnackbarIsShownWhenWorkerFails() {
    composeTestRule.mainClock.autoAdvance = false
    disableInternet()
    composeTestRule.mainClock.autoAdvance = true
    composeTestRule.onNodeWithText("Please check your internet connection").assertIsDisplayed()
  }

  @Test
  @FlakyTest
  fun testSnackbarIsShownWhenWorkerFailsAndRetryWorks() {
    composeTestRule.mainClock.autoAdvance = false
    disableInternet()
    composeTestRule.mainClock.autoAdvance = true
    composeTestRule.onNodeWithText("Please check your internet connection").run {
      assertIsDisplayed()
    }
    composeTestRule.mainClock.autoAdvance = false
    enableInternet()
    composeTestRule.onNodeWithText("Retry").performClick()
//    TestUtil.delay(5)
  }

  private fun disableInternet() {
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi disable")
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data disable")
  }
  private fun enableInternet() {
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi enable")
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data enable")
  }
}
