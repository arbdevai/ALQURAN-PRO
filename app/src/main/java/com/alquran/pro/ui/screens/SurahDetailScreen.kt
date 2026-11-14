package com.alquran.pro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.audio.AudioPlayerState
import com.alquran.pro.data.model.BookmarkItem
import com.alquran.pro.data.model.SuratDetailDto
import com.alquran.pro.ui.components.AyahCard
import com.alquran.pro.ui.components.SkeletonBox
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.GoldAccent
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.theme.TextArabic
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun SurahDetailScreen(
    surahDetail: SuratDetailDto?,
    isLoading: Boolean,
    bookmarks: List<BookmarkItem>,
    audioState: AudioPlayerState,
    onBack: () -> Unit,
    onPlayVerseAudio: (surahNomor: Int, ayatNomor: Int, surahName: String, url: String) -> Unit,
    onPlayFullSurahAudio: (surahNomor: Int, surahName: String, url: String) -> Unit,
    onToggleBookmark: (BookmarkItem) -> Unit,
    onMarkLastRead: (surahNomor: Int, surahLatin: String, surahArab: String, nomorAyat: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Navigasi Kembali Murni Native Android Gesture (Tanpa Tombol Kembali Fisik/Layar)
    BackHandler {
        onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
            .statusBarsPadding()
    ) {
        if (isLoading || surahDetail == null) {
            // Skeleton Shimmer saat memuat ayat
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(110.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(60.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                repeat(4) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(160.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        } else {
            // Header Info Surat & Audio Full Player Bar (Bersih, Tanpa Tombol Kembali di Layar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SURAT KE-${surahDetail.nomor}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = surahDetail.namaLatin,
                                color = TextHighContrast,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "${surahDetail.tempatTurun} • ${surahDetail.jumlahAyat} Ayat • ${surahDetail.arti}",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Text(
                            text = surahDetail.nama,
                            color = TextArabic,
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bar Tombol Audio Murottal Surat Lengkap
                    val fullAudioUrl = surahDetail.audioFull["05"]
                        ?: surahDetail.audioFull["01"]
                        ?: surahDetail.audioFull.values.firstOrNull() ?: ""

                    if (fullAudioUrl.isNotBlank()) {
                        val isPlayingFull = audioState.isPlaying &&
                                audioState.currentSurahNomor == surahDetail.nomor &&
                                audioState.currentAyatNomor == null

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isPlayingFull) EmeraldContainer else DarkSurfaceContainer)
                                .clickable {
                                    onPlayFullSurahAudio(surahDetail.nomor, surahDetail.namaLatin, fullAudioUrl)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingFull) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Putar Murottal Penuh",
                                    tint = if (isPlayingFull) TextHighContrast else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isPlayingFull) "Jeda Murottal Penuh" else "Putar Murottal Penuh (Qari Misyari Rasyid)",
                                    color = TextHighContrast,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.Audiotrack,
                                contentDescription = "Audio Tilawah",
                                tint = if (isPlayingFull) TextHighContrast else GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Daftar Ayat
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Basmalah (Ditampilkan untuk semua surat kecuali Surat ke-9 At-Taubah)
                if (surahDetail.nomor != 9 && surahDetail.nomor != 1) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                                .padding(vertical = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                color = TextArabic,
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Serif,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(surahDetail.ayat, key = { it.nomorAyat }) { ayat ->
                    val isBookmarked = bookmarks.any {
                        it.surahNomor == surahDetail.nomor && it.nomorAyat == ayat.nomorAyat
                    }
                    val isPlayingVerse = audioState.isPlaying &&
                            audioState.currentSurahNomor == surahDetail.nomor &&
                            audioState.currentAyatNomor == ayat.nomorAyat

                    val verseAudioUrl = ayat.audio["05"]
                        ?: ayat.audio["01"]
                        ?: ayat.audio.values.firstOrNull() ?: ""

                    AyahCard(
                        ayat = ayat,
                        isBookmarked = isBookmarked,
                        isPlaying = isPlayingVerse,
                        onPlayAudioClick = {
                            onPlayVerseAudio(
                                surahDetail.nomor,
                                ayat.nomorAyat,
                                surahDetail.namaLatin,
                                verseAudioUrl
                            )
                        },
                        onBookmarkClick = {
                            onToggleBookmark(
                                BookmarkItem(
                                    surahNomor = surahDetail.nomor,
                                    surahNamaLatin = surahDetail.namaLatin,
                                    surahNamaArab = surahDetail.nama,
                                    nomorAyat = ayat.nomorAyat,
                                    teksArabSnippet = ayat.teksArab,
                                    teksIndonesiaSnippet = ayat.teksIndonesia
                                )
                            )
                        },
                        onMarkLastReadClick = {
                            onMarkLastRead(
                                surahDetail.nomor,
                                surahDetail.namaLatin,
                                surahDetail.nama,
                                ayat.nomorAyat
                            )
                        }
                    )
                }
            }
        }
    }
}
