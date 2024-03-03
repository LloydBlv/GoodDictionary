package com.example.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
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
import com.example.dictionary_sync.DictionarySyncStateWatcher

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToWordsList: () -> Unit
) {
    val viewModel = hiltViewModel<SplashViewModel>()
    val syncState: DictionarySyncStateWatcher.State by viewModel.state.collectAsState()

    Scaffold(modifier = Modifier,

        content = { SplashContent(modifier.padding(it)) })
    LaunchedEffect(key1 = syncState) {
        Log.e("worker", "syncState=${syncState}")
        if (syncState is DictionarySyncStateWatcher.State.Loaded) {
            navigateToWordsList.invoke()
            return@LaunchedEffect
        } else if (syncState is DictionarySyncStateWatcher.State.Progress) {
            val percent = (syncState as DictionarySyncStateWatcher.State.Progress).percent
            if (percent > 1) {
                navigateToWordsList.invoke()
            }
        }
    }

}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.good_dictionary),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.displayMedium
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomCenter)
        )
    }
}