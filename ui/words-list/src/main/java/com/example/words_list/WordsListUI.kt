@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.words_list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordsListScreen(
    modifier: Modifier = Modifier,
    navigateToDetails: (id: Long) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            WordsScreenContent(
                modifier = modifier.padding(it),
                navigateToDetails
            )
        }
    )

}

@Composable
private fun WordsScreenContent(modifier: Modifier, navigateToDetails: (id: Long) -> Unit) {
    Column(modifier = modifier) {
        SearchBarUi()
        WordsListUi(navigateToDetails = navigateToDetails)
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
                        contentDescription = stringResource(id = R.string.clear_query)
                    )
                }
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        })

}

@Composable
private fun ColumnScope.WordsListUi(
    modifier: Modifier = Modifier,
    navigateToDetails: (id: Long) -> Unit
) {
    val viewModel = hiltViewModel<WordsListViewModel>()
    val lazyPagingItems = viewModel.items2.collectAsLazyPagingItems()

    val count = lazyPagingItems.itemCount
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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

            lazyPagingItems.loadState.refresh is LoadState.NotLoading -> {
                WordsList(
                    modifier = modifier,
                    count = count,
                    lazyPagingItems = lazyPagingItems,
                    navigateToDetails = navigateToDetails
                )
            }
        }
    }
}


@Composable
private fun WordsList(
    modifier: Modifier,
    count: Int,
    lazyPagingItems: LazyPagingItems<PagingUiItem>,
    navigateToDetails: (id: Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            count = count,
            contentType = { lazyPagingItems.itemContentType { it is PagingUiItem.Header } }
        ) { index ->
            when (val item = lazyPagingItems[index]) {
                is PagingUiItem.Header -> HeaderComposable(item)
                is PagingUiItem.WordItem -> WordItemComposable(item, navigateToDetails)
                else -> {}
            }
        }
    }
}

@Composable
fun LazyItemScope.HeaderComposable(header: PagingUiItem.Header) {
    Text(
        text = "${header.letter}",
        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp)
    )
}

@Composable
fun LazyItemScope.WordItemComposable(
    wordItem: PagingUiItem.WordItem,
    navigateToDetails: (id: Long) -> Unit
) {
    androidx.compose.material3.ListItem(
        modifier = Modifier.clickable { navigateToDetails.invoke(wordItem.id) },
        headlineContent = {
            Text(
                text = wordItem.word,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)
            )
        },
        trailingContent = {
            IconButton(onClick = { navigateToDetails.invoke(wordItem.id) }, content = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.show_word_details)
                )
            })
        }
    )

}