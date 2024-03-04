package com.example.wordslist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopButton(onClick: () -> Unit) {
  Box(
    Modifier
      .fillMaxSize()
      .padding(bottom = 50.dp),
    Alignment.BottomCenter,
  ) {
    Button(
      onClick = { onClick() },
      modifier = Modifier
        .shadow(10.dp, shape = CircleShape)
        .clip(shape = CircleShape)
        .size(65.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = Color.Green,
      ),
    ) {
      Icon(Icons.Filled.KeyboardArrowUp, "arrow up")
    }
  }
}

@Composable
internal fun WordsList(
  modifier: Modifier,
  count: Int,
  lazyPagingItems: LazyPagingItems<PagingUiItem>,
  navigateToDetails: (id: Long) -> Unit,
) {
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()
  LazyColumn(
    state = listState,
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    items(
      count = count,
      contentType = { lazyPagingItems.itemContentType { it is PagingUiItem.Header } },
    ) { index ->
      when (val item = lazyPagingItems[index]) {
        is PagingUiItem.Header -> HeaderComposable(item)
        is PagingUiItem.WordItem -> WordItemComposable(item, navigateToDetails)
        else -> {}
      }
    }
  }

  val showButton by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex > 0
    }
  }
  AnimatedVisibility(
    visible = showButton,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    ScrollToTopButton(onClick = {
      scope.launch {
        listState.animateScrollToItem(0)
      }
    })
  }
}

@Composable
private fun LazyItemScope.HeaderComposable(header: PagingUiItem.Header) {
  Text(
    text = "${header.letter}",
    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
  )
}

@Composable
private fun LazyItemScope.WordItemComposable(
  wordItem: PagingUiItem.WordItem,
  navigateToDetails: (id: Long) -> Unit,
) {
  androidx.compose.material3.ListItem(
    modifier = Modifier.clickable { navigateToDetails.invoke(wordItem.id) },
    headlineContent = {
      Text(
        text = wordItem.word,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
      )
    },
    trailingContent = {
      IconButton(onClick = { navigateToDetails.invoke(wordItem.id) }, content = {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = stringResource(id = R.string.show_worddetails),
        )
      })
    },
  )
}
