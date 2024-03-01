package com.example.words_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.data.AppDatabase
import com.example.data.SyncDictionaryDefault
import com.example.data.WordEntity
import com.example.data.WordsDao
import com.example.words_list.UiState.LoadingDictionary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.system.measureTimeMillis


sealed interface PagingUiItem {
    fun key(): String
    data class Header(val letter: Char,
                      val id: String = UUID.randomUUID().toString(),) : PagingUiItem {
        override fun key(): String {
            return "${letter}_${id}"
        }
    }
    data class WordItem(val word: String, val id: Long) : PagingUiItem {
        override fun key(): String {
            return "${word}_${id}"
        }
    }
}

sealed interface UiState {
    object Failure : UiState {

    }

    sealed interface LoadingDictionary: UiState {
        data object Loading: LoadingDictionary
        data class Loaded(val loadTimeMs: Long): LoadingDictionary
        data class Failed(val loadTimeMs: Long): LoadingDictionary
    }
    data class PagingState(val data: PagingData<PagingUiItem>): UiState

}

@HiltViewModel
class WordsListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dao: WordsDao
) : ViewModel() {

    private val _dictionaryLoadState = MutableStateFlow<LoadingDictionary>(LoadingDictionary.Loading)
    val query = MutableStateFlow("")
    val items2  = query
        .debounce(300)
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 100),
                initialKey = null,
                pagingSourceFactory = { dao.filtered1("%$query%") }
            ).flow
                .map(::createPagingData)
                .cachedIn(viewModelScope)
        }

    private fun createPagingData(pagingData: PagingData<WordEntity>) =
        pagingData.map { PagingUiItem.WordItem(it.word, it.rowid) }
            .insertSeparators { before, after ->
                when {
                    before == null && after != null -> {
                        PagingUiItem.Header(after.word.first().uppercaseChar())
                    }// before first item
                    before == null -> null

                    after == null -> null // after last item
                    before.word.first().uppercaseChar() != after.word.first()
                        .uppercaseChar() -> {
                        // Insert header when first letter changes
                        PagingUiItem.Header(after.word.first().uppercaseChar())
                    }

                    else -> null // No separator
                }
            }


    init {
        viewModelScope.launch {
            val words = withContext(Dispatchers.IO) {
                dao.getAllWords()
            }
            if (words.isNotEmpty()) {
                Log.e("loadState", "going to emit loaded")

                _dictionaryLoadState.update { LoadingDictionary.Loaded(1) }
                return@launch
            }
            if (words.isEmpty()) {
                val sync = SyncDictionaryDefault(database)
                println("starting sync")
                withContext(Dispatchers.IO) {
                    val time = measureTimeMillis {
                        context.assets.open("words_alpha3.txt")
                            .bufferedReader()
                            .useLines {
                                sync.insertUsingSqlite(it.sorted())
                            }
                    }
                    _dictionaryLoadState.update { LoadingDictionary.Loaded(time) }

                }
                println("sync is finished")
            }

        }

        fun generateRandomStringOfLengthTwo(): String {
            val allowedChars = ('a'..'z') + ('A'..'Z') // Define the characters to choose from
            return (1..2) // Generate a string of length 2
                .map { allowedChars.random() } // Select a random character for each position
                .joinToString("") // Join them into a string
        }
//        viewModelScope.launch {
//            while (true) {
//                delay(5.seconds)
//                query.update { generateRandomStringOfLengthTwo() }
//            }
//        }
    }
    fun onQueryChanged(query: String) {
        this@WordsListViewModel.query.update { query }
    }


}