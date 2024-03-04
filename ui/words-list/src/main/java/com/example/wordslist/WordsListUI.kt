@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wordslist

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.dictionarysync.DictionarySyncStateWatcher

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordsListScreen(
  modifier: Modifier = Modifier,
  navigateToDetails: (id: Long) -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    modifier = Modifier.fillMaxSize(),
    content = {
      WordsScreenContent(
        modifier = modifier.padding(it),
        navigateToDetails,
      )
    },
  )
}

@Composable
private fun WordsScreenContent(modifier: Modifier, navigateToDetails: (id: Long) -> Unit) {
  Column(modifier = modifier) {
    SearchBarUi()
    WordsListUi(
      navigateToDetails = navigateToDetails,
      modifier = Modifier.weight(1.0f),
    )
    SyncStateUi()
  }
}

@Composable
fun SyncStateUi() {
  val viewModel = hiltViewModel<WordsListViewModel>()
  val syncState: DictionarySyncStateWatcher.State by viewModel.state.collectAsState()
  var isVisible by remember { mutableStateOf(false) }
  AnimatedVisibility(
    enter = fadeIn(animationSpec = tween(300)) +
      slideInVertically(
        // Slide in from 50 pixels above the baseline
        initialOffsetY = { -50 },
        animationSpec = tween(300),
      ),
    exit = fadeOut(animationSpec = tween(300)) +
      slideOutVertically(
        // Slide out to 50 pixels below the baseline
        targetOffsetY = { 50 },
        animationSpec = tween(300),
      ),
    visible = syncState is DictionarySyncStateWatcher.State.Progress,
  ) {
    var message by remember {
      mutableStateOf("")
    }
    var progress by remember {
      mutableFloatStateOf(0.0f)
    }
    Column(
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    ) {
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(2.dp),
      )
      Text(text = message, modifier = Modifier)
    }
    LaunchedEffect(key1 = syncState) {
      if (syncState is DictionarySyncStateWatcher.State.Progress) {
        val percent =
          (syncState as DictionarySyncStateWatcher.State.Progress).percent

        progress = percent / 100.0f
        if (percent == 100) {
          message = "Dictionary synced success!"
        } else {
          message = "Syncing... $percent%"
        }
      }
    }
  }
}

@Composable
fun SearchBarUi(modifier: Modifier = Modifier) {
  val viewModel = hiltViewModel<WordsListViewModel>()
  val query by viewModel.query.collectAsState(initial = "")
  DockedSearchBar(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp),
    query = query,
    onQueryChange = viewModel::onQueryChanged,
    onSearch = viewModel::onQueryChanged,
    active = false,
    onActiveChange = {},
    content = {},
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = { viewModel.onQueryChanged("") }) {
          Icon(
            Icons.Default.Clear,
            contentDescription = stringResource(id = R.string.clear_query),
          )
        }
      }
    },
    placeholder = {
      Text(text = stringResource(id = R.string.search))
    },
  )
}

@Composable
private fun ColumnScope.WordsListUi(
  modifier: Modifier = Modifier,
  navigateToDetails: (id: Long) -> Unit,
) {
  val viewModel = hiltViewModel<WordsListViewModel>()
  val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

  val count = lazyPagingItems.itemCount
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    when {
      lazyPagingItems.loadState.refresh == LoadState.Loading -> {
        CircularProgressIndicator(modifier = Modifier.size(32.dp))
      }

      lazyPagingItems.loadState.refresh is LoadState.Error -> {
        Text(text = "Something went wrong")
      }

      lazyPagingItems.itemCount == 0 -> {
        Text(text = "No results found")
      }
    }
    WordsList(
      modifier = modifier,
      count = count,
      lazyPagingItems = lazyPagingItems,
      navigateToDetails = navigateToDetails,
    )
  }
}
