package com.alquran.pro.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alquran.pro.ui.components.AudioMiniPlayer
import com.alquran.pro.ui.components.FloatingDockBar
import com.alquran.pro.ui.components.MainTab
import com.alquran.pro.ui.screens.BookmarksScreen
import com.alquran.pro.ui.screens.PrayerTimesScreen
import com.alquran.pro.ui.screens.QuranListScreen
import com.alquran.pro.ui.screens.SurahDetailScreen
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.viewmodel.PrayerViewModel
import com.alquran.pro.ui.viewmodel.QuranViewModel

@Composable
fun MainApp(
    quranViewModel: QuranViewModel = viewModel(),
    prayerViewModel: PrayerViewModel = viewModel()
) {
    val quranState by quranViewModel.uiState.collectAsStateWithLifecycle()
    val prayerState by prayerViewModel.uiState.collectAsStateWithLifecycle()
    val lastRead by quranViewModel.lastRead.collectAsStateWithLifecycle()
    val bookmarks by quranViewModel.bookmarks.collectAsStateWithLifecycle()
    val audioState by quranViewModel.audioPlayer.playerState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainTab.QURAN) }
    var viewingSurahNomor by remember { mutableStateOf<Int?>(null) }

    // Navigasi Kembali Murni Menggunakan Gesture Native Android
    if (viewingSurahNomor != null) {
        BackHandler {
            viewingSurahNomor = null
            quranViewModel.clearSurahDetail()
        }
    } else if (currentTab != MainTab.QURAN) {
        BackHandler {
            currentTab = MainTab.QURAN
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OledBlack
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OledBlack)
        ) {
            // Konten Layar Utama dengan Transisi Halus
            AnimatedContent(
                targetState = viewingSurahNomor,
                transitionSpec = {
                    if (targetState != null) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width / 3 } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "ScreenTransition",
                modifier = Modifier.fillMaxSize()
            ) { activeSurah ->
                if (activeSurah != null) {
                    SurahDetailScreen(
                        surahDetail = quranState.surahDetail,
                        isLoading = quranState.isLoadingDetail,
                        bookmarks = bookmarks,
                        audioState = audioState,
                        onBack = {
                            viewingSurahNomor = null
                            quranViewModel.clearSurahDetail()
                        },
                        onPlayVerseAudio = { surahNomor, ayatNomor, surahName, url ->
                            quranViewModel.audioPlayer.playVerse(surahNomor, ayatNomor, surahName, url)
                        },
                        onPlayFullSurahAudio = { surahNomor, surahName, url ->
                            quranViewModel.audioPlayer.playFullSurah(surahNomor, surahName, url)
                        },
                        onToggleBookmark = { item ->
                            quranViewModel.toggleBookmark(item)
                        },
                        onMarkLastRead = { surahNomor, surahLatin, surahArab, nomorAyat ->
                            quranViewModel.recordLastRead(surahNomor, surahLatin, surahArab, nomorAyat)
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        when (currentTab) {
                            MainTab.QURAN -> {
                                QuranListScreen(
                                    surahs = quranState.filteredSurahs,
                                    isLoading = quranState.isLoadingSurahs,
                                    searchQuery = quranState.searchQuery,
                                    lastRead = lastRead,
                                    onSearchChange = { quranViewModel.onSearchQueryChange(it) },
                                    onSurahClick = { nomor ->
                                        viewingSurahNomor = nomor
                                        quranViewModel.loadSurahDetail(nomor)
                                    }
                                )
                            }
                            MainTab.PRAYER -> {
                                PrayerTimesScreen(
                                    state = prayerState.dashboard,
                                    showCityDialog = prayerState.showCityDialog,
                                    citySearchQuery = prayerState.citySearchQuery,
                                    citySearchResults = prayerState.searchResults,
                                    isSearchingCity = prayerState.isSearching,
                                    onRefresh = { prayerViewModel.loadSchedule() },
                                    onOpenCityDialog = { prayerViewModel.openCityDialog() },
                                    onCloseCityDialog = { prayerViewModel.closeCityDialog() },
                                    onCitySearchQueryChange = { prayerViewModel.onCityQueryChange(it) },
                                    onCitySelected = { kota -> prayerViewModel.selectCity(kota) }
                                )
                            }
                            MainTab.BOOKMARKS -> {
                                BookmarksScreen(
                                    bookmarks = bookmarks,
                                    onBookmarkClick = { nomor ->
                                        viewingSurahNomor = nomor
                                        quranViewModel.loadSurahDetail(nomor)
                                    },
                                    onRemoveBookmark = { item ->
                                        quranViewModel.toggleBookmark(item)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Overlay: Mini Player & Floating Dock
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Audio Mini Player (Muncul dinamis saat tilawah ayat / surat aktif)
                AnimatedVisibility(
                    visible = audioState.currentSurahNomor != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    AudioMiniPlayer(
                        state = audioState,
                        onPlayPauseClick = {
                            if (audioState.isPlaying) {
                                quranViewModel.audioPlayer.pause()
                            } else {
                                quranViewModel.audioPlayer.resume()
                            }
                        },
                        onCloseClick = {
                            quranViewModel.audioPlayer.stop()
                        }
                    )
                }

                // Floating Dock Bar (Disembunyikan saat membaca surat agar kanvas baca 100% lapang)
                AnimatedVisibility(
                    visible = viewingSurahNomor == null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    FloatingDockBar(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            }
        }
    }
}
