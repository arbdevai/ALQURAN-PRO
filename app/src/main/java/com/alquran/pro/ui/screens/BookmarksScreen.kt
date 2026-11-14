package com.alquran.pro.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.data.model.BookmarkItem
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.GoldAccent
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.theme.TextArabic
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMediumContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkItem>,
    onBookmarkClick: (surahNomor: Int) -> Unit,
    onRemoveBookmark: (BookmarkItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "ARSIP PRIBADI",
                color = EmeraldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Ayat Tersimpan",
                color = TextHighContrast,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "${bookmarks.size} Ayat ditandai",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.dp, DarkSurfaceBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmarks,
                            contentDescription = "Belum Ada Bookmark",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Belum Ada Ayat Tersimpan",
                        color = TextHighContrast,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tekan ikon bookmark pada ayat mana pun saat membaca surat untuk menyimpannya di sini.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(bookmarks, key = { "${it.surahNomor}_${it.nomorAyat}" }) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                            .clickable { onBookmarkClick(item.surahNomor) }
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSurfaceContainer)
                                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Ayat ${item.nomorAyat}",
                                            color = EmeraldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = item.surahNamaLatin,
                                        color = TextHighContrast,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = item.surahNamaArab,
                                        color = TextArabic,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(DarkSurfaceContainer)
                                            .clickable { onRemoveBookmark(item) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.BookmarkRemove,
                                            contentDescription = "Hapus Bookmark",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = item.teksIndonesiaSnippet,
                                color = TextMediumContrast,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
