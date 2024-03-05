@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.worddetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worddetails.DetailViewModel.DetailUiState

@Composable
fun WordDetailScreen(
  modifier: Modifier = Modifier,
  onWordDeleted: () -> Unit,
  onBackPressed: () -> Unit,
) {
  val viewModel = hiltViewModel<DetailViewModel>()
  val state: DetailUiState by viewModel.state.collectAsState()
  WordDetailContent(
    modifier = modifier,
    state = state,
    onBackPressed = onBackPressed,
    onWordDeleted = onWordDeleted,
    onDeleteWordClicked = viewModel::deleteWord
  )
}

@Composable
internal fun WordDetailContent(
  modifier: Modifier = Modifier,
  state: DetailUiState,
  onBackPressed: () -> Unit,
  onWordDeleted: () -> Unit,
  onDeleteWordClicked: () -> Unit
) {
  val snackbarHostState = remember { SnackbarHostState() }

  Scaffold(
    topBar = {
      DetailTopAppBar(onBackPressed, state)
    },
    modifier = modifier,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) {
    DetailContentView(
      modifier = modifier.padding(it),
      isLoading = state.isLoading,
      word = state.word?.word
    )
  }

  val removeThisWordText = stringResource(id = R.string.remove_this_word)
  LaunchedEffect(Unit) {
    val result = snackbarHostState.showSnackbar(
      removeThisWordText,
      actionLabel = "Yes, Remove!",
      withDismissAction = true,
      duration = SnackbarDuration.Indefinite,
    )
    if (result == SnackbarResult.ActionPerformed) {
      onDeleteWordClicked()
      onWordDeleted.invoke()
    }
  }
}

@Composable
private fun DetailContentView(
  modifier: Modifier,
  isLoading: Boolean,
  word: String?,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    when {
      isLoading -> CircularProgressIndicator()

      word != null ->
        Text(
          text = word,
          fontSize = 55.sp,
        )
    }
  }
}

@Composable
private fun DetailTopAppBar(
  onBackPressed: () -> Unit,
  state: DetailUiState
) {
  TopAppBar(
    navigationIcon = {
      IconButton(onClick = onBackPressed) {
        Icon(
          Icons.AutoMirrored.Default.ArrowBack,
          contentDescription = stringResource(id = R.string.go_back),
        )
      }
    },
    title = {
      Text(
        text = if (state.count > 0) {
          "Word details from ${state.count} words"
        } else {
          "Word details"
        },
      )
    },
  )
}
