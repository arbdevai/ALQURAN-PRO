package com.alquran.pro.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alquran.pro.audio.QuranAudioPlayer
import com.alquran.pro.data.model.BookmarkItem
import com.alquran.pro.data.model.LastReadItem
import com.alquran.pro.data.model.SuratDetailDto
import com.alquran.pro.data.model.SuratSummaryDto
import com.alquran.pro.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuranUiState(
    val surahs: List<SuratSummaryDto> = emptyList(),
    val filteredSurahs: List<SuratSummaryDto> = emptyList(),
    val searchQuery: String = "",
    val isLoadingSurahs: Boolean = true,
    val surahDetail: SuratDetailDto? = null,
    val isLoadingDetail: Boolean = false,
    val errorMessage: String? = null
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuranRepository(application.applicationContext)
    val audioPlayer = QuranAudioPlayer(application.applicationContext)

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    val lastRead: StateFlow<LastReadItem?> = repository.lastReadFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookmarks: StateFlow<List<BookmarkItem>> = repository.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSurahList()
    }

    fun loadSurahList(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSurahs = true, errorMessage = null) }
            repository.getSuratList(forceRefresh).collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.update { current ->
                            val filtered = filterSurahs(list, current.searchQuery)
                            current.copy(
                                surahs = list,
                                filteredSurahs = filtered,
                                isLoadingSurahs = false,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { current ->
                            current.copy(
                                isLoadingSurahs = false,
                                errorMessage = err.message ?: "Gagal memuat surat"
                            )
                        }
                    }
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            val filtered = filterSurahs(current.surahs, query)
            current.copy(
                searchQuery = query,
                filteredSurahs = filtered
            )
        }
    }

    fun loadSurahDetail(surahNomor: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, surahDetail = null, errorMessage = null) }
            repository.getSuratDetail(surahNomor).collect { result ->
                result.fold(
                    onSuccess = { detail ->
                        _uiState.update { current ->
                            current.copy(
                                surahDetail = detail,
                                isLoadingDetail = false,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { current ->
                            current.copy(
                                isLoadingDetail = false,
                                errorMessage = err.message ?: "Gagal memuat detail surat"
                            )
                        }
                    }
                )
            }
        }
    }

    fun clearSurahDetail() {
        _uiState.update { it.copy(surahDetail = null, isLoadingDetail = false) }
    }

    fun toggleBookmark(item: BookmarkItem) {
        viewModelScope.launch {
            repository.toggleBookmark(item)
        }
    }

    fun recordLastRead(surahNomor: Int, surahLatin: String, surahArab: String, nomorAyat: Int) {
        viewModelScope.launch {
            repository.saveLastRead(
                LastReadItem(
                    surahNomor = surahNomor,
                    surahNamaLatin = surahLatin,
                    surahNamaArab = surahArab,
                    nomorAyat = nomorAyat
                )
            )
        }
    }

    private fun filterSurahs(list: List<SuratSummaryDto>, query: String): List<SuratSummaryDto> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return list
        return list.filter {
            it.namaLatin.lowercase().contains(q) ||
            it.arti.lowercase().contains(q) ||
            it.tempatTurun.lowercase().contains(q) ||
            it.nomor.toString() == q
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
