package com.example.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.DictionaryState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToWordsList: () -> Unit
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = state) {
        if (state is DictionaryState.Loaded) {
            delay(500)
            navigateToWordsList()
        }
    }
    Scaffold(modifier = Modifier) {
        Box(
            modifier = modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.good_dictionary),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.displayMedium
            )
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                val text = when (state) {

                    DictionaryState.Loading -> {
                        "Loading..."
                    }

                    DictionaryState.ParsingItems -> {
                        "Parsing items "
                    }

                    is DictionaryState.Loaded -> {
                        "Loaded ${(state as DictionaryState.Loaded).wordsCount} words!"
                    }
                }
                Text(text = text)
            }

        }
    }

}