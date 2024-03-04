package com.example.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SplashScreenUiTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `when state is loaded navigate to words list callback is called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Loaded,
    )
    verify { navigateToWordsList.invoke() }
  }

  @Test
  fun `when state is Progress==1 navigate to words list callback is called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Progress(100),
    )
    verify { navigateToWordsList.invoke() }
  }

  @Test
  fun `when state is Progress==0 navigate to words list callback is NOT called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Progress(0),
    )
    verify { navigateToWordsList wasNot Called }
  }

  @Test
  fun `when state is Progress==1 navigate to words list callback is NOT called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Progress(1),
    )
    verify { navigateToWordsList wasNot Called }
  }

  @Test
  fun `when state is Progress==2 navigate to words list callback is NOT called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Progress(2),
    )
    verify { navigateToWordsList.invoke() }
  }

  @Test
  fun `when state is Loading navigate to words list callback is NOT called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Loading,
    )
    verify { navigateToWordsList wasNot Called }
  }

  @Test
  fun `when state is Failed navigate to words list callback is NOT called`() {
    val navigateToWordsList: () -> Unit = mockk(relaxed = true)
    setContent(
      navigateToWordsList = navigateToWordsList,
      syncState = SplashViewModel.UiState.Failure(null),
    )
    verify { navigateToWordsList wasNot Called }
  }

  @Test
  fun `when state is Failed, snackbar is shown`() {
    setContent(syncState = SplashViewModel.UiState.Failure(null))
    composeTestRule.onNode(
      hasText("Something went wrong!"),
      useUnmergedTree = true,
    ).assertIsDisplayed()
  }

  @Test
  fun `when state is Failed with message, snackbar is shown`() {
    setContent(syncState = SplashViewModel.UiState.Failure("Terrible failure"))
    composeTestRule.onNode(
      hasText("Terrible failure"),
      useUnmergedTree = true,
    ).assertIsDisplayed()
  }

  @Test
  fun `when state is Failure, snackbar is retry button is shown`() {
    setContent(syncState = SplashViewModel.UiState.Failure("Terrible failure"))
    composeTestRule.onNode(
      hasText("Retry"),
      useUnmergedTree = true,
    ).assertIsDisplayed()
  }

  @Test
  fun `when state is Failure, snackbar retry button is clickable`() {
    val onRetryClicked: () -> Unit = mockk(relaxed = true)
    setContent(
      syncState = SplashViewModel.UiState.Failure("Terrible failure"),
      onRetryClicked = onRetryClicked,
    )
    composeTestRule.onNode(
      hasText("Retry"),
      useUnmergedTree = true,
    ).performClick()
    composeTestRule.waitForIdle()
    verify { onRetryClicked() }
  }

  @Test
  fun `when state is Not Failure with message, snackbar is NOT shown`() {
    setContent(syncState = SplashViewModel.UiState.Loading)
    composeTestRule.onNode(
      hasText("Retry"),
      useUnmergedTree = true,
    ).assertDoesNotExist()
  }

  private fun setContent(
    navigateToWordsList: () -> Unit = {},
    onRetryClicked: () -> Unit = {},
    syncState: SplashViewModel.UiState,
  ) {
    composeTestRule.setContent {
      SplashScreen(
        navigateToWordsList = navigateToWordsList,
        syncState = syncState,
        onRetryClicked = onRetryClicked,
      )
    }
  }
}
