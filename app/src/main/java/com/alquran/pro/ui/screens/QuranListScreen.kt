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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.pro.data.model.LastReadItem
import com.alquran.pro.data.model.SuratSummaryDto
import com.alquran.pro.ui.components.SkeletonBox
import com.alquran.pro.ui.components.SurahCard
import com.alquran.pro.ui.theme.DarkSurface
import com.alquran.pro.ui.theme.DarkSurfaceBorder
import com.alquran.pro.ui.theme.DarkSurfaceContainer
import com.alquran.pro.ui.theme.EmeraldContainer
import com.alquran.pro.ui.theme.EmeraldPrimary
import com.alquran.pro.ui.theme.GoldAccent
import com.alquran.pro.ui.theme.OledBlack
import com.alquran.pro.ui.theme.TextArabic
import com.alquran.pro.ui.theme.TextHighContrast
import com.alquran.pro.ui.theme.TextMediumContrast
import com.alquran.pro.ui.theme.TextMuted

@Composable
fun QuranListScreen(
    surahs: List<SuratSummaryDto>,
    isLoading: Boolean,
    searchQuery: String,
    lastRead: LastReadItem?,
    onSearchChange: (String) -> Unit,
    onSurahClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Header Aplikasi
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "AL-QUR'AN PRO",
                color = EmeraldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Mushaf Al-Qur'an",
                color = TextHighContrast,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "Kemenag RI • EQuran v2",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Kotak Pencarian Surat
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = "Cari surat (contoh: Al-Kahf, Yasin, 18)...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Cari Surat",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Hapus Pencarian",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onSearchChange("") }
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = EmeraldPrimary,
                    focusedTextColor = TextHighContrast,
                    unfocusedTextColor = TextHighContrast
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Hero Terakhir Dibaca
        if (lastRead != null && searchQuery.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkSurface)
                    .border(1.dp, EmeraldPrimary, RoundedCornerShape(18.dp))
                    .clickable { onSurahClick(lastRead.surahNomor) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "Terakhir Dibaca",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "TERAKHIR DIBACA",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = lastRead.surahNamaLatin,
                            color = TextHighContrast,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Ayat ${lastRead.nomorAyat}",
                            color = GoldAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldContainer)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Lanjutkan",
                            color = TextHighContrast,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Daftar Surat atau Skeleton Loading
        if (isLoading && surahs.isEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(8) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(76.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        } else if (surahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada surat yang cocok dengan '${searchQuery}'",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(surahs, key = { it.nomor }) { surah ->
                    SurahCard(
                        surah = surah,
                        onClick = { onSurahClick(surah.nomor) }
                    )
                }
            }
        }
    }
}
