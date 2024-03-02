package com.example.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.domain.DictionaryWord
import com.example.domain.usecases.GetFilteredWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

sealed interface UiState {
    object Failure : UiState

    sealed interface LoadingDictionary : UiState {
        data object Loading : LoadingDictionary
        data class Loaded(val loadTimeMs: Long) : LoadingDictionary
        data class Failed(val loadTimeMs: Long) : LoadingDictionary
    }

    data class PagingState(val data: PagingData<PagingUiItem>) : UiState

}

@HiltViewModel
class WordsListViewModel @Inject constructor(
    private val getFilteredWordUseCase: GetFilteredWordsUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")
    val items2 = query
        .debounce(300)
        .flatMapLatest { query ->
            getFilteredWordUseCase.invoke(query)
                .map(::createPagingData)
                .cachedIn(viewModelScope)
        }

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
        this@WordsListViewModel.query.update { query }
    }


}