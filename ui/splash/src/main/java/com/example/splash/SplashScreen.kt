package com.example.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToWordsList: () -> Unit
) {
    val viewModel = hiltViewModel<SplashViewModel>()
    val syncState by viewModel.state.collectAsState()
    SplashScreen(
        modifier = modifier,
        syncState = syncState,
        navigateToWordsList = navigateToWordsList,
        onRetryClicked = viewModel::onRetryClicked
    )
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    syncState: SplashViewModel.UiState,
    navigateToWordsList: () -> Unit,
    onRetryClicked: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier,
        content = { SplashContent(modifier.padding(it), syncState) })


    LaunchedEffect(key1 = syncState) {
        when (syncState) {
            is SplashViewModel.UiState.Loaded -> {
                navigateToWordsList.invoke()
            }

            is SplashViewModel.UiState.Progress -> {
                val percent = syncState.percent
                if (percent > 1) {
                    navigateToWordsList.invoke()
                }
            }

            is SplashViewModel.UiState.Failure -> {
                val result = snackbarHostState.showSnackbar(
                    message = syncState.message ?: "Something went wrong!",
                    actionLabel = "Retry",
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onRetryClicked.invoke()
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier, state: SplashViewModel.UiState) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.good_dictionary),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.displayMedium
        )

        if (state.isLoading()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}