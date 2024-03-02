package com.example.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.dictionary_sync.DictionarySyncStateWatcher
import com.example.domain.DictionaryWord
import com.example.domain.usecases.GetFilteredWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject


sealed interface PagingUiItem {
    fun key(): String
    data class Header(
        val letter: Char,
        val id: String = UUID.randomUUID().toString(),
    ) : PagingUiItem {
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

@HiltViewModel
class WordsListViewModel @Inject constructor(
    private val getFilteredWordUseCase: GetFilteredWordsUseCase,
    stateWatcher: DictionarySyncStateWatcher
) : ViewModel() {

    val state: StateFlow<DictionarySyncStateWatcher.State> = stateWatcher.watch()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DictionarySyncStateWatcher.State.Loading
        )
    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query.asStateFlow()

    val pagingDataFlow = query
        .debounce(300)
        .flatMapLatest { query ->
            getFilteredWordUseCase.invoke(query)
                .map(::createPagingData)
                .cachedIn(viewModelScope)
        }.debounce(500)

    private fun createPagingData(pagingData: PagingData<DictionaryWord>) =
        pagingData.map { PagingUiItem.WordItem(it.word, it.id) }
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

    fun onQueryChanged(query: String) {
        this@WordsListViewModel._query.update { query }
    }


}