@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication

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

@Composable
fun WordDetailScreen(
    modifier: Modifier = Modifier, id: Long,
    onWordDeleted: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<DetailViewModel>()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                title = {
                    Text(text = "Word details")
                })
        },
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Box(
            modifier = modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is DetailViewModel.DetailUiState.Loaded -> {
                    Text(
                        text = (state as DetailViewModel.DetailUiState.Loaded).word.word,
                        fontSize = 55.sp
                    )

                }

                DetailViewModel.DetailUiState.Loading -> {
                    CircularProgressIndicator()

                }
            }
        }
    }
    LaunchedEffect(Unit) {
        val result = snackbarHostState.showSnackbar(
            "Remove word?",
            actionLabel = "Yes,Remove!",
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.deleteWord(id)
            onWordDeleted.invoke()
        }

    }

}